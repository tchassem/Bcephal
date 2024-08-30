using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class GrilleRowType
    {

        public static GrilleRowType ALL = new GrilleRowType("ALL", "All");
        public static GrilleRowType RECONCILIATED = new GrilleRowType("RECONCILIATED", "Reconciliated row only");
        public static GrilleRowType NOT_RECONCILIATED = new GrilleRowType("NOT_RECONCILIATED", "Not reconciliated row only");
        public static GrilleRowType ON_HOLD = new GrilleRowType("ON_HOLD", "On hold row only");
        public static GrilleRowType BILLED = new GrilleRowType("BILLED", "Billed row only");
        public static GrilleRowType NOT_BILLED = new GrilleRowType("NOT_BILLED", "Not billed row only");
        public static GrilleRowType DRAFT = new GrilleRowType("DRAFT", "Draft row only");
        public static GrilleRowType VALIDATED = new GrilleRowType("VALIDATED", "Validated row only");
        public static GrilleRowType NOT_BOOKED = new GrilleRowType("NOT_BOOKED", "Not booked row only");
        public static GrilleRowType BOOKED = new GrilleRowType("BOOKED", "Booked row only");


        public String label;
        public String code;


        public GrilleRowType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }


        public override String ToString()
        {
            return label;
        }

        public static GrilleRowType GetByCode(String code)
        {
            if (code == null) return NOT_RECONCILIATED;
            if (ALL.code.Equals(code)) return ALL;
            if (RECONCILIATED.code.Equals(code)) return RECONCILIATED;
            if (NOT_RECONCILIATED.code.Equals(code)) return NOT_RECONCILIATED;
            if (ON_HOLD.code.Equals(code)) return ON_HOLD;
            if (BILLED.code.Equals(code)) return BILLED;
            if (NOT_BILLED.code.Equals(code)) return NOT_BILLED;
            if (DRAFT.code.Equals(code)) return DRAFT;
            if (VALIDATED.code.Equals(code)) return VALIDATED;
            if (BOOKED.code.Equals(code)) return BOOKED;
            if (NOT_BOOKED.code.Equals(code)) return NOT_BOOKED;
            return null;
        }

        public static ObservableCollection<GrilleRowType> GetRecoTypes()
        {
            ObservableCollection<GrilleRowType> conditions = new ObservableCollection<GrilleRowType>();
            conditions.Add(ALL);
            conditions.Add(RECONCILIATED);
            conditions.Add(NOT_RECONCILIATED);
            conditions.Add(ON_HOLD);
            return conditions;
        }

        public static ObservableCollection<GrilleRowType> GetBillingTypes()
        {
            ObservableCollection<GrilleRowType> conditions = new ObservableCollection<GrilleRowType>();
            conditions.Add(ALL);
            conditions.Add(BILLED);
            conditions.Add(NOT_BILLED);
            return conditions;
        }

        public static ObservableCollection<GrilleRowType> GetPostingTypes()
        {
            ObservableCollection<GrilleRowType> conditions = new ObservableCollection<GrilleRowType>();
            conditions.Add(ALL);
            conditions.Add(BOOKED);
            conditions.Add(NOT_BOOKED);
            return conditions;
        }

    }
}
