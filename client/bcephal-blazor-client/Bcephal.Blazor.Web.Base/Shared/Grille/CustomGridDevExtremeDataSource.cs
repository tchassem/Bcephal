using System.Threading;
using DevExpress.Blazor;
using System;
using System.Threading.Tasks;
using Bcephal.Models.Base;
using System.Collections.ObjectModel;

namespace Bcephal.Blazor.Web.Base.Shared.AbstractComponent
{
    
    public class CustomGridDevExtremeDataSource<T> : GridCustomDataSource
    {
        Func<Task<BrowserDataPage<T>>> HttpRequestFunc { get; set; }
        Func<int, Task> PageSizeChanged { get; set; }
        Func<bool, bool> GetStatus { get; set; }
        BrowserDataPage<T> Page { get; set; }

        bool InPage { get; set; } = false;

        public CustomGridDevExtremeDataSource(Func<Task<BrowserDataPage<T>>> httpRequestFunc, Func<bool, bool> getStatus, Func<int, Task> pageSizeChanged)
        {
            HttpRequestFunc = httpRequestFunc;
            GetStatus = getStatus;
            PageSizeChanged = pageSizeChanged;
        }
        public override async Task<int> GetItemCountAsync(GridCustomDataSourceCountOptions options, CancellationToken cancellationToken)
        {
            try
            {
                if ((Page == null && !InPage) || GetStatus.Invoke(false))
                {
                    InPage = true;
                    Page = await HttpRequestFunc?.Invoke();
                }
                return Page.TotalItemCount;
            }
            catch
            {
                return 1;
            }
        }

        public override async Task<System.Collections.IList> GetItemsAsync(GridCustomDataSourceItemsOptions options, CancellationToken cancellationToken)
        {
            try
            {
                if (Page != null && options.Count != Page.PageSize && Page.PageSize != -1 && options.Count != -1 && Page.PageSize != 0)
                {
                    await PageSizeChanged?.Invoke(options.Count);
                }
            }
            catch { }
            if (Page != null)
            {
                return Page.Items;
            }
            return new ObservableCollection<T>();
        }

        public override Task<System.Collections.IList> GetSummaryAsync(GridCustomDataSourceSummaryOptions options, CancellationToken cancellationToken)
        {
            return base.GetSummaryAsync(options, cancellationToken);
        }
    }
}