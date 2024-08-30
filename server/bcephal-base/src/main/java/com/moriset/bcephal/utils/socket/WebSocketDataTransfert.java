package com.moriset.bcephal.utils.socket;

import lombok.Data;

@Data
public class WebSocketDataTransfert {
	public enum Decision {
		END, STOP, CONTINUE, NEW, CLOSE;

		public boolean isEnd() {
			return this == END;
		}

		public boolean isStop() {
			return this == STOP;
		}

		public boolean isContinue() {
			return this == CONTINUE;
		}

		public boolean isNew() {
			return this == NEW;
		}
		
		public boolean isClose() {
			return this == CLOSE;
		}
	}

	private byte[] data;
	private Decision decision;
	private DataType type;
	private String  name;
	private String  folder;
	private Long userId;
	private String  remotePath;

	public WebSocketDataTransfert() {
		decision = Decision.CONTINUE;
	}

	public WebSocketDataTransfert(byte[] data, DataType type) {
		this();
		this.data = data;
		this.type = type;
	}

	public WebSocketDataTransfert(byte[] data, Decision decision, DataType type) {
		this(data, type);
		this.decision = decision;
	}

	public WebSocketDataTransfert(byte[] data, String decision, DataType type) {
		this(data, type);
		this.decision = Decision.valueOf(decision);
	}
}
