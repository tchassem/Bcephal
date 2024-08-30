using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Base.Accounting
{
    public class PostingSign
    {

        public static PostingSign DEBIT = new PostingSign("D", "D");
        public static PostingSign CREDIT = new PostingSign("C", "C");

        public String label { get; set; }
        public String code { get; set; }

        public PostingSign(String code, String label)
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

        public static PostingSign GetByCode(String code)
        {
            if (code == null) return null;
            if (DEBIT.code.Equals(code)) return DEBIT;
            if (CREDIT.code.Equals(code)) return CREDIT;
            return null;
        }

        public static ObservableCollection<PostingSign> GetAll()
        {
            ObservableCollection<PostingSign> signs = new ObservableCollection<PostingSign>();
            signs.Add(CREDIT);
            signs.Add(DEBIT);
            return signs;
        }

    }
}
