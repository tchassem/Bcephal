using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Clients
{
    public class ClientStatus
    {
        public static ClientStatus ACTIVE = new ClientStatus("ACTIVE", "Active");
        public static ClientStatus TRIAL = new ClientStatus("TRIAL", "Trial");
        public static ClientStatus SUSPENDED = new ClientStatus("SUSPENDED", "Suspended");
        public static ClientStatus END_OF_TRIAL = new ClientStatus("END_OF_TRIAL", "End of trial");
        public static ClientStatus CLOSED = new ClientStatus("CLOSED", "Closed");


        public String label;
        public String code;


        public ClientStatus(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsActive()
        {
            return this == ACTIVE;
        }

        public bool IsTrial()
        {
            return this == TRIAL;
        }

        public bool IsSuspended()
        {
            return this == SUSPENDED;
        }

        public bool IsEndOfTrial()
        {
            return this == END_OF_TRIAL;
        }

        public bool IsClosed()
        {
            return this == CLOSED;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ClientStatus GetByLabel(string label)
        {
            if (label == null) return null;
            if (ACTIVE.label.Equals(label)) return ACTIVE;
            if (TRIAL.label.Equals(label)) return TRIAL;
            if (SUSPENDED.label.Equals(label)) return SUSPENDED;
            if (END_OF_TRIAL.label.Equals(label)) return END_OF_TRIAL;
            if (CLOSED.label.Equals(label)) return CLOSED;
            return null;
        }

        public static ClientStatus GetByCode(string code)
        {
            if (code == null) return null;
            if (ACTIVE.code.Equals(code)) return ACTIVE;
            if (TRIAL.code.Equals(code)) return TRIAL;
            if (SUSPENDED.code.Equals(code)) return SUSPENDED;
            if (END_OF_TRIAL.code.Equals(code)) return END_OF_TRIAL;
            if (CLOSED.code.Equals(code)) return CLOSED;
            return null;
        }

        public static ObservableCollection<ClientStatus> GetAll()
        {
            ObservableCollection<ClientStatus> operators = new ObservableCollection<ClientStatus>();
            operators.Add(ClientStatus.ACTIVE);
            operators.Add(ClientStatus.TRIAL);
            operators.Add(ClientStatus.SUSPENDED);
            operators.Add(ClientStatus.END_OF_TRIAL);
            operators.Add(ClientStatus.CLOSED);
            return operators;
        }

    }

    public static class ClientStatusExtention
    {
        public static ObservableCollection<string> GetAll(this ClientStatus clientStatus, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("ACTIVE"));
            operators.Add(Localize?.Invoke("TRIAL"));
            operators.Add(Localize?.Invoke("SUSPENDED"));
            operators.Add(Localize?.Invoke("END_OF_TRIAL"));
            operators.Add(Localize?.Invoke("CLOSED"));
            return operators;
        }

        public static string GetText(this ClientStatus clientStatus, Func<string, string> Localize)
        {
            if (clientStatus.IsActive())
            {
                return Localize?.Invoke("ACTIVE");
            }
            if (clientStatus.IsActive())
            {
                return Localize?.Invoke("TRIAL");
            }
            if (clientStatus.IsActive())
            {
                return Localize?.Invoke("SUSPENDED");
            }
            if (clientStatus.IsActive())
            {
                return Localize?.Invoke("END_OF_TRIAL");
            }
            if (clientStatus.IsActive())
            {
                return Localize?.Invoke("CLOSED");
            }
            return null;
        }

        public static ClientStatus GetClientStatus(this ClientStatus clientStatus, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("ACTIVE")))
                {
                    return ClientStatus.ACTIVE;
                }
                if (text.Equals(Localize?.Invoke("TRIAL")))
                {
                    return ClientStatus.TRIAL;
                }
                if (text.Equals(Localize?.Invoke("SUSPENDED")))
                {
                    return ClientStatus.SUSPENDED;
                }
                if (text.Equals(Localize?.Invoke("END_OF_TRIAL")))
                {
                    return ClientStatus.END_OF_TRIAL;
                }
                if (text.Equals(Localize?.Invoke("CLOSED")))
                {
                    return ClientStatus.CLOSED;
                }
            }
            return null;
        }
    }
}
