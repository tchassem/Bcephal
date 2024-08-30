using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Accounting
{
    public class PostingStatus
    {

        public static PostingStatus DRAFT = new PostingStatus("DRAFT", "DRAFT");
        public static PostingStatus VALIDATED = new PostingStatus("VALIDATED", "VALIDATED");

        public String label;
        public String code;

        public PostingStatus(String code, String label)
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

        public static PostingStatus GetByCode(String code)
        {
            if (code == null) return null;
            if (DRAFT.code.Equals(code)) return DRAFT;
            if (VALIDATED.code.Equals(code)) return VALIDATED;
            return null;
        }

        public static ObservableCollection<PostingStatus> GetAll()
        {
            ObservableCollection<PostingStatus> statusList = new ObservableCollection<PostingStatus>();
            statusList.Add(PostingStatus.DRAFT);
            statusList.Add(PostingStatus.VALIDATED);
            return statusList;
        }

    }
}
