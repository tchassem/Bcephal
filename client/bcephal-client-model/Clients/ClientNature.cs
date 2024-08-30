using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public class ClientNature
    {
        public static ClientNature COMPANY = new ClientNature("COMPANY", "Company");
        public static ClientNature PERSONAL = new ClientNature("PERSONAL", "Personal");

        public String label;
        public String code;


        public ClientNature(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsCompany()
        {
            return this == COMPANY;
        }

        public bool IsPersonal()
        {
            return this == PERSONAL;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ClientNature GetByLabel(string label)
        {
            if (label == null) return null;
            if (COMPANY.label.Equals(label)) return COMPANY;
            if (PERSONAL.label.Equals(label)) return PERSONAL;
            return null;
        }

        public static ClientNature GetByCode(string code)
        {
            if (code == null) return null;
            if (COMPANY.code.Equals(code)) return COMPANY;
            if (PERSONAL.code.Equals(code)) return PERSONAL;
            return null;
        }

        public static ObservableCollection<ClientNature> GetAll()
        {
            ObservableCollection<ClientNature> operators = new ObservableCollection<ClientNature>();
            operators.Add(ClientNature.COMPANY);
            operators.Add(ClientNature.PERSONAL);
            return operators;
        }

        

    }
}
