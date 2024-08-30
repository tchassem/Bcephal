using Bcephal.Models.Accounting;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Base.Accounting
{
    public class PostingEditorData : EditorData<Posting>
    {

        public ObservableCollection<Account> accounts { get; set; }

    }
}
