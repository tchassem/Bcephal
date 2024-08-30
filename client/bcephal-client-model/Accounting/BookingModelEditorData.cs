using System.Collections.ObjectModel;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingModelEditorData : EditorData<BookingModel>
    {

        public ObservableCollection<Nameable> repositoryColumns { get; set; }

    }
}
