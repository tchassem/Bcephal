using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
  public class PrivilegeObserverControl
    {
        private readonly AppState AppState;
        private readonly SourcingPrivilegeObserver SourcingPrivilegeObserver;
        private readonly InitiationPrivilegeObserver InitiationPrivilegeObserver;
        private readonly TransformationPrivilegeObserver TransformationPrivilegeObserver;
        private readonly SettingsPrivilegeObserver SettingsPrivilegeObserver;
        private readonly ProjectPrivilegeObserver ProjectPrivilegeObserver;
        private readonly ReportingPrivilegeObserver ReportingPrivilegeObserver;
        private readonly DashboardPrivilegeObserver DashboardPrivilegeObserver;
        private readonly DataManagerPrivilegeObserver DataManagerPrivilegeObserver;
        private readonly ReconciliationPrivilegeObserver ReconciliationPrivilegeObserver;
        private readonly MessengerPrivilegeObserver MessengerPrivilegeObserver;
        private readonly AdministrationPrivilegeObserver AdministrationPrivilegeObserver;

        public PrivilegeObserverControl(AppState AppState)
        {
            this.AppState = AppState;
            SourcingPrivilegeObserver = new(AppState);
            InitiationPrivilegeObserver = new(AppState);
            TransformationPrivilegeObserver = new(AppState);
            SettingsPrivilegeObserver = new(AppState);
            ProjectPrivilegeObserver = new(AppState);
            ReportingPrivilegeObserver = new(AppState);
            DashboardPrivilegeObserver = new(AppState);
            DataManagerPrivilegeObserver = new(AppState);
            ReconciliationPrivilegeObserver = new(AppState);
            MessengerPrivilegeObserver = new(AppState);
            AdministrationPrivilegeObserver = new(AppState);
        }
        public bool HasPrivilege(string uri)
        {
            if (SourcingPrivilegeObserver.IsSourcing(uri))
            {
                return SourcingPrivilegeObserver.HasPrivilege(uri);
            }
            if (InitiationPrivilegeObserver.IsInitiation(uri))
            {
                return InitiationPrivilegeObserver.HasPrivilege(uri);
            }
            if (TransformationPrivilegeObserver.IsTransformation(uri))
            {
                return TransformationPrivilegeObserver.HasPrivilege(uri);
            }
            if (SettingsPrivilegeObserver.IsSettings(uri))
            {
                return SettingsPrivilegeObserver.HasPrivilege(uri);
            }
            if (ProjectPrivilegeObserver.IsProject(uri))
            {
                return ProjectPrivilegeObserver.HasPrivilege(uri);
            }
            if (ReportingPrivilegeObserver.IsReporting(uri))
            {
                return ReportingPrivilegeObserver.HasPrivilege(uri);
            }
            if (DashboardPrivilegeObserver.IsDashboard(uri))
            {
                return DashboardPrivilegeObserver.HasPrivilege(uri);
            }
            if (DataManagerPrivilegeObserver.IsDataManager(uri))
            {
                return DataManagerPrivilegeObserver.HasPrivilege(uri);
            }
            if (ReconciliationPrivilegeObserver.IsReconciliation(uri))
            {
                return ReconciliationPrivilegeObserver.HasPrivilege(uri);
            } 
            if (MessengerPrivilegeObserver.IsMessenger(uri))
            {
                return MessengerPrivilegeObserver.HasPrivilege(uri);
            }
             if (AdministrationPrivilegeObserver.IsAdministration(uri))
            {
                return AdministrationPrivilegeObserver.HasPrivilege(uri);
            }
            return true;
        }
    }
}
