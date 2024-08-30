using System.Threading;
using DevExpress.Blazor;
using System;
using System.Threading.Tasks;
using Bcephal.Models.Base;
using System.Collections.ObjectModel;
using Bcephal.Blazor.Web.Base.Shared.Grille;
using DevExpress.Data.Linq;
using DevExpress.Data.ODataLinq;
using System.Collections;

namespace Bcephal.Blazor.Web.Base.Shared.AbstractComponent
{
    
    public class CustomGridDevExtremeNewDataSource<T> : GridCustomDataSource
    {
        private Func<int, int, Task> HttpRequestFunc { get; set; }
        private Func<ObservableCollection<T>> ManualyDataFunc { get; set; }
        private Func<GridCustomDataSourceSummaryOptions, Task<System.Collections.IList>> SummaryDataFunc { get; set; }
        private Func<Task> Refresh { get; set; }
        private Func<BrowserDataPage<T>> PageFunc { get; set; }
        private Func<bool, bool> GetStatus { get; set; }
        private bool HasSearch { get; set; } = false;
        private int MaxItemCount { get; set; } = 26;


        public CustomGridDevExtremeNewDataSource(Func<int, int, Task> httpRequestFunc, Func<BrowserDataPage<T>> pageFunc,
            Func<Task>  refresh, Func<bool, bool> getStatus, Func<ObservableCollection<T>> manualyDataFunc,
            Func<GridCustomDataSourceSummaryOptions, Task<System.Collections.IList>> summaryDataFunc)
        {
            HttpRequestFunc = httpRequestFunc;
            PageFunc = pageFunc;
            Refresh = refresh;
            GetStatus = getStatus;
            ManualyDataFunc = manualyDataFunc;
            SummaryDataFunc = summaryDataFunc;
          }
        public override Task<int> GetItemCountAsync(GridCustomDataSourceCountOptions options, CancellationToken cancellationToken)
        {
            if (ManualyDataFunc != null)
            {
                return Task.FromResult(ManualyDataFunc.Invoke().Count);
            }
            else
            {
                if (PageFunc() == null)
                {
                    return Task.FromResult(1);
                }
                return Task.FromResult(PageFunc().TotalItemCount);
            }
        }

        public override async  Task<System.Collections.IList> GetItemsAsync(GridCustomDataSourceItemsOptions options, CancellationToken cancellationToken)
        {
            if (!HasSearch && GetStatus != null && GetStatus.Invoke(false))
            {
                if (ManualyDataFunc != null)
                {
                    return ManualyDataFunc.Invoke();
                }
                else
                {
                    int page = options.StartIndex / (options.Count == 0 ? 1 : options.Count);
                    bool isGotoLastMaxPage = (MaxItemCount - options.Count) == options.StartIndex;
                    if (isGotoLastMaxPage)
                    {
                        page = PageFunc().CurrentPage + 1;
                    }
                    await HttpRequestFunc?.Invoke(page, options.Count);
                    if (Refresh != null)
                    {
                        HasSearch = true;
                        await Refresh();
                    }
                    return new ObservableCollection<T>();
                }
            }
            else
            {
                HasSearch = false;
                return PageFunc().Items;
            }
        }

        public override Task<System.Collections.IList> GetSummaryAsync(GridCustomDataSourceSummaryOptions options, CancellationToken cancellationToken)
        {
            if(SummaryDataFunc != null)
            {
                return  SummaryDataFunc(options);
            }
            return  base.GetSummaryAsync(options, cancellationToken);
        }
    }
}