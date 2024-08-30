package com.moriset.bcephal.messenger.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor;
import org.apache.activemq.broker.region.virtual.VirtualTopic;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.AuthorizationEntry;
import org.apache.activemq.security.AuthorizationPlugin;
import org.apache.activemq.security.DefaultAuthorizationMap;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.security.TempDestinationAuthorizationEntry;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.transport.TransportFactory;
import org.apache.activemq.util.IOHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.messenger.properties.HostAddress;
import com.moriset.bcephal.messenger.properties.ServerProperties;
import com.moriset.bcephal.messenger.properties.SharedUser;

import lombok.extern.slf4j.Slf4j;

@Primary
@Component
@Slf4j
public class BcephalActivemqServer {
	protected BrokerService broker;
	@Autowired
	public HostAddress hostAddress;

	@Autowired
	public SharedUser sharedUser;

	@Autowired
	public ServerProperties serverProperties;

	public boolean USING_REMOTE_HOST = false;
	protected String TRANSPORT_NAME = "openwire-remote-tcp-server";
	protected String SCHEDULER_SUPPORT_NAME = "data-SchedulerSupport";
	protected String PATTERN_URL_FAILOVER = "static:failover:masterslave:(%s)";
	protected String FORWARD_TO_QUEUE_NAME = "forward-to--bcephal-remote--tcp--other-queue";
	protected String FORWARD_TO_TOPIC_NAME = "forward-to--bcephal-remote--tcp--other-topic";

	/**
	 * 
	 */
	public void builder() {
		try {
			broker = createBrocker();
			broker.setBrokerName(serverProperties.getBrockerName());
			broker.setUseShutdownHook(false);
			broker.setDeleteAllMessagesOnStartup(serverProperties.isClearAllMessage());
			broker.setPlugins(buildOtherPlugin());
			// using virtual destination
			broker.setDestinationInterceptors(buildVirtualDestination());
			broker.setDataDirectoryFile(new File(serverProperties.getDbPath()));
			broker.setPersistenceAdapter(getKahaDBPersistenceAdapter(serverProperties.getDbPath()));
			broker.setDestinationPolicy(getPolicyMap());
			if (USING_REMOTE_HOST) {
				List<NetworkConnector> items = configureLocalNetworkConnector(hostAddress.getIp(),
						hostAddress.getTcpPort());
				broker.addNetworkConnector(items.get(0));
				broker.addNetworkConnector(items.get(1));
			}
			BrokerRegistry.getInstance().bind(serverProperties.getBrockerBindName(), broker);
			log.debug("Strating active MQ...");
			System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");
			if (serverProperties.isStart()) {
				start();
			}
			log.info("Active MQ strated!");

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void start() throws Exception {
		if (broker != null) {
			broker.start();
			broker.waitUntilStarted();
		}
	}

	protected BrokerService createBrocker() throws IOException, Exception {
		BrokerService broker = new BrokerService();
		List<TransportConnector> trans = createlocaleConnector(hostAddress.getTcpPort(),hostAddress.getStompPort());
		for(int offset = 0; offset < trans.size() ; offset++) {
			trans.get(offset).setName(TRANSPORT_NAME + offset);
			trans.get(offset).setUpdateClusterClients(true);
			trans.get(offset).setRebalanceClusterClients(true);
			trans.get(offset).setUpdateClusterClientsOnRemove(true);
			broker.addConnector(trans.get(offset));
		}
		return broker;
	}

	public void stopBrocker() throws Exception {
		broker.stop();
		broker.waitUntilStopped();
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected KahaDBPersistenceAdapter getKahaDBPersistenceAdapter(String path) throws Exception {
		KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
		persistenceAdapter.setDirectory(new File(path, serverProperties.getDbName()));
		broker.setDataDirectory(new File(path, SCHEDULER_SUPPORT_NAME).getPath());
		persistenceAdapter.setForceRecoverIndex(true);
		persistenceAdapter.setEnableIndexWriteAsync(true);
		persistenceAdapter.setIgnoreMissingJournalfiles(true);
		persistenceAdapter.setConcurrentStoreAndDispatchTopics(true);
		persistenceAdapter.setBrokerService(broker);
		File dir = persistenceAdapter.getDirectory();
		if (dir != null) {
			IOHelper.deleteFile(new File(dir, "db.data"));
		}
		return persistenceAdapter;
	}

	/**
	 * 
	 * @return
	 */
	protected PolicyMap getPolicyMap() {
		PolicyMap policyMap = new PolicyMap();
		PolicyEntry policyEntry = new PolicyEntry();
		policyEntry.setUseCache(false);
		policyMap.setDefaultEntry(policyEntry);
		return policyMap;
	}

	protected DestinationInterceptor[] buildVirtualDestination() {
		VirtualDestinationInterceptor virtualDestinationInterceptor = new VirtualDestinationInterceptor();
		ArrayList<VirtualTopic> virtualDestinations = new ArrayList<VirtualTopic>();
		for (String dest : serverProperties.getVirtualDestination()) {
			VirtualTopic virtualDestination = new VirtualTopic();
			virtualDestination.setName(dest.substring(0, dest.indexOf(".")) + ".>");
			virtualDestination.setPrefix(dest);
			virtualDestination.setSelectorAware(false);
			virtualDestinations.add(virtualDestination);
		}
		virtualDestinationInterceptor
				.setVirtualDestinations(virtualDestinations.toArray(new VirtualTopic[virtualDestinations.size()]));
		return new DestinationInterceptor[] { virtualDestinationInterceptor };
	}

	/**
	 * 
	 * @param uri
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private DiscoveryNetworkConnector configurNetQueue(URI uri, String name) throws IOException {
		DiscoveryNetworkConnector connector = new DiscoveryNetworkConnector(uri);
		connector.setName(name);
		connector.setDynamicOnly(false);
		connector.setDecreaseNetworkConsumerPriority(false);
		connector.setConduitSubscriptions(false);
		connector.setNetworkTTL(3);
		connector.setUserName(sharedUser.getName());
		connector.setPassword(sharedUser.getPassword());
		connector.setDuplex(true);
		connector.setStaticBridge(true);
		connector.setSuppressDuplicateQueueSubscriptions(true);
		return connector;
	}

	private DiscoveryNetworkConnector configurNetTopic(URI uri, String name) throws IOException {
		DiscoveryNetworkConnector connector = new DiscoveryNetworkConnector(uri);
		connector.setName(name);
		connector.setDynamicOnly(false);
		connector.setDecreaseNetworkConsumerPriority(false);
		connector.setConduitSubscriptions(false);
		connector.setNetworkTTL(3);
		connector.setUserName(sharedUser.getName());
		connector.setPassword(sharedUser.getPassword());
		connector.setDuplex(true);
		connector.setStaticBridge(true);
		connector.setSuppressDuplicateTopicSubscriptions(true);
		connector.setUseCompression(true);
		return connector;
	}

	protected List<NetworkConnector> configureLocalNetworkConnector(String remoteIpAddress, int remoteTcpPort)
			throws Exception {
		List<NetworkConnector> connectors = new ArrayList<>();
		if (!(remoteIpAddress == null || remoteIpAddress.trim().isEmpty())) {
			String uri = String.format(PATTERN_URL_FAILOVER, getRemoteURI("tcp", remoteIpAddress, remoteTcpPort));
			connectors.add(configurNetQueue(new URI(uri), FORWARD_TO_QUEUE_NAME));
			connectors.add(configurNetTopic(new URI(uri), FORWARD_TO_TOPIC_NAME));
		}
		return connectors;
	}

	protected List<TransportConnector> createRemoteConnector(String ip, int tcpPort, int stompPort)
			throws Exception, IOException, URISyntaxException {
		List<TransportConnector> items = new ArrayList<>();
		items.add(new TransportConnector(TransportFactory.bind(new URI(getRemoteURI("tcp", ip, tcpPort)))));
		return items;
	}

	protected List<TransportConnector> createlocaleConnector(Integer tcpPort, Integer stompPort)
			throws Exception, IOException, URISyntaxException {
		List<TransportConnector> items = new ArrayList<>();
		items.add(new TransportConnector(TransportFactory.bind(new URI(getLocalURI("tcp", tcpPort)))));
		items.add(new TransportConnector(TransportFactory.bind(new URI(getLocalURI("stomp", stompPort)))));
		return items;
	}

	protected String getRemoteURI(String protocol,String ip, int port) {
		return String.format("%s://%s:%s", protocol, ip, port);
	}

	protected String getLocalURI(String protocol,int port) {
		return String.format("%s://0.0.0.0:%s", protocol, port);
	}

	protected BrokerPlugin[] buildOtherPlugin() {
		@SuppressWarnings("rawtypes")
		ArrayList<DestinationMapEntry> authorizationEntries = new ArrayList<>();
		DefaultAuthorizationMap authorizationMap = new DefaultAuthorizationMap();
		AuthorizationPlugin authorizationPlugin = new AuthorizationPlugin();
		ArrayList<BrokerPlugin> plugins = new ArrayList<BrokerPlugin>();

		try {
			authorizationEntries.add(buildQueueAuthorizationEntry(">", sharedUser.getGuests(), sharedUser.getGroup(),
					sharedUser.getGroup()));
			authorizationEntries.add(buildTopicAuthorizationEntry(">", sharedUser.getGuests(), sharedUser.getGroup(),
					sharedUser.getGroup()));
			authorizationEntries.add(buildTopicAuthorizationEntry("ActiveMQ.Advisory.>", sharedUser.getGuests(),
					sharedUser.getName(), sharedUser.getGroup()));

			TempDestinationAuthorizationEntry tempEntry = new TempDestinationAuthorizationEntry();
			tempEntry.setRead(sharedUser.getGuests());
			tempEntry.setWrite(sharedUser.getGroup());
			tempEntry.setAdmin(sharedUser.getGroup());

			authorizationPlugin.setMap(authorizationMap);
			authorizationMap.setAuthorizationEntries(authorizationEntries);
			authorizationMap.setTempDestinationAuthorizationEntry(tempEntry);
			plugins.add(authorizationPlugin);

			List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
			users.add(new AuthenticationUser(sharedUser.getName(), sharedUser.getPassword(), sharedUser.getGuests()));
			SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);

			authenticationPlugin.setAnonymousAccessAllowed(false);
			plugins.add(authenticationPlugin);

			BrokerPlugin[] array = new BrokerPlugin[plugins.size()];
			return plugins.toArray(array);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new BrokerPlugin[0];
	}

	protected AuthorizationEntry buildQueueAuthorizationEntry(String queue, String read, String write, String admin)
			throws Exception {
		AuthorizationEntry entry = new AuthorizationEntry();
		entry.setAdmin(admin);
		entry.setQueue(queue);
		entry.setRead(read);
		entry.setWrite(write);
		return entry;
	}

	protected AuthorizationEntry buildTopicAuthorizationEntry(String topic, String read, String write, String admin)
			throws Exception {
		AuthorizationEntry entry = new AuthorizationEntry();
		entry.setTopic(topic);
		entry.setRead(read);
		entry.setWrite(write);
		entry.setAdmin(admin);
		return entry;
	}

}
