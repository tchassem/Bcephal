using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public class ClientType
    {
        public static ClientType PRIVILEGE = new ClientType("PRIVILEGE", "PRIVILEGE");
        public static ClientType SILVER = new ClientType("SILVER", "SILVER");
        public static ClientType GOLD = new ClientType("GOLD", "GOLD");
        public static ClientType PREMIUM = new ClientType("PREMIUM", "PREMIUM");
        public static ClientType BUSINESS = new ClientType("BUSINESS", "BUSINESS");


        public String label;
        public String code;

        public ClientType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsPRIVILEGE()
        {
            return this == PRIVILEGE;
        }

        public bool IsSILVER()
        {
            return this == SILVER;
        }

        public bool IsGOLD()
        {
            return this == GOLD;
        }

        public bool IsPREMIUM()
        {
            return this == PREMIUM;
        }

        public bool IsBUSINESS()
        {
            return this == BUSINESS;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ClientType GetByLabel(string label)
        {
            if (label == null) return null;
            if (PRIVILEGE.label.Equals(label)) return PRIVILEGE;
            if (SILVER.label.Equals(label)) return SILVER;
            if (GOLD.label.Equals(label)) return GOLD;
            if (PREMIUM.label.Equals(label)) return PREMIUM;
            if (BUSINESS.label.Equals(label)) return BUSINESS;
            return null;
        }

        public static ClientType GetByCode(string code)
        {
            if (code == null) return null;
            if (PRIVILEGE.code.Equals(code)) return PRIVILEGE;
            if (SILVER.code.Equals(code)) return SILVER;
            if (GOLD.code.Equals(code)) return GOLD;
            if (PREMIUM.code.Equals(code)) return PREMIUM;
            if (BUSINESS.code.Equals(code)) return BUSINESS;
            return null;
        }

        public static ObservableCollection<ClientType> GetAll()
        {
            ObservableCollection<ClientType> operators = new ObservableCollection<ClientType>();
            operators.Add(ClientType.PRIVILEGE);
            operators.Add(ClientType.SILVER);
            operators.Add(ClientType.GOLD);
            operators.Add(ClientType.PREMIUM);
            operators.Add(ClientType.BUSINESS);
            return operators;
        }

    }
}
