using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using System.ComponentModel;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public class RecoInputNewGridComponent : InputNewGridComponent
    {


        [Parameter]
        public Action<BrowserDataFilter> FilterHandler { get; set; }

        [Parameter]
        public bool RecoShowSelectionColumnVisible { get; set; } = true;

        [Parameter]
        public ObservableCollection<GridItem> SelectionManualyDatas_ { get; set; }

        [Parameter]
        public bool CanRefreshPartialGrid { get; set; } = false;

        protected bool CanRefreshGridStatus_ { get; set; } = false;

        protected override bool ShouldRender()
        {
            if (!StateReadyFirst_)
            {
                return CanRefreshGridStatus_;
            }
            return base.ShouldRender();
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            if (UsingManualyData)
            {
                ShowPager = false;
                PageSizeSelector = false;
                AllowAllRow = true;
                ShowsAll_ = true;
                ShowFilterRow = false;
            }
            else
            {
                FilterHandler?.Invoke(getFilter());
            }
        }


        private bool IniCustomFilter { get; set; } = false;

        protected override Task BuildFilter(BrowserDataFilter filter)
        {
            if (!IniCustomFilter)
            {
                FilterHandler?.Invoke(filter);
                IniCustomFilter = true;
            }
            return base.BuildFilter(filter);
        }
        #region reco filter


        public Task ClearGrid()
        {

            if (UsingManualyData)
            {

                if (ManualyData != null)
                {
                    ManualyData.Clear();
                }
                page_.Items = new ObservableCollection<GridItem>();
                SelectedDataItemsChanged(new List<GridItem>());
            }
            DxGridRef.ClearSelection();
            return RefreshGrid_();
        }


        public Task RefreshGrid()
        {
            if (ManualyData != null)
            {
                ManualyData.Clear();
            }
            DxGridRef.DeselectAllOnPage();
            SelectedDataItemsChanged(new List<GridItem>());
            return RefreshGrid_();
        }
        
        private List<GridItem> GetGridItemElement(Func<GridItem, bool> func, ObservableCollection<GridItem> datas)
        {
            List<GridItem> items = new();
            if (datas != null && func != null)
            {
                foreach (var item in datas)
                {
                    if (func.Invoke(item))
                    {
                        items.Add(item);
                    }
                }
            }
            return items;
        }

        public Task RefreshSelection(List<long> ids = null)
        {
            if (UsingManualyData)
            {
                return RefreshGrid_().ContinueWith(t=>Task.Run(()=> DxGridRef.SelectAllOnPage()));
            }
            else
            {
                if (ids != null)
                {
                    if (page_ != null)
                    {
                        List<GridItem> gridItems = new();
                        ids.ForEach(id =>
                        {
                            Func<GridItem, bool> Ivalue = ele => ele.Id.HasValue && ele.Id.Value == id;
                            var item = GetGridItemElement(Ivalue, page_.Items);
                            if (item != null && item.Any())
                            {
                                gridItems.Add(item.FirstOrDefault());
                            }
                        });
                        SelectedDataItemsChanged(gridItems);
                    }
                }
                return Task.CompletedTask;
            }
        }

        public List<long> GetSelectedItem(string side)
        {
            List<long> items = new();
            if (UsingManualyData)
            {
                foreach(var item in GetManualyDatas())
                {
                    if (item.Id.HasValue && string.IsNullOrWhiteSpace(item.Side) &&  item.Side.Equals(side))
                    {
                        items.Add(item.Id.Value);
                    }
                }
            }
            return items;
        }

        public Task AddNewRow(List<long> ids, string side)
        {
            filter.Ids = new ObservableCollection<long?>();
            GetManualyDatas().RemoveAll(item => item.Side.Equals(side));
            Func<GridItem, bool> condition = (GridItem item) => !item.Side.Equals(side) /*|| ids.Contains((item as GridItem).Id.Value)*/;
            SelectedDataItems = SelectedDataItems.Cast<GridItem>().Where(condition).ToList();
            foreach (var id in ids)
            {
                filter.Ids.Add(id);
            }
            try
            {
                if (ids.Count > 0)
                {
                    AppState.ShowLoadingStatus();
                    filter.ShowAll = false;
                    filter.PageSize = ids.Count;
                    Task<BrowserDataPage<GridItem>> tas = SearchRows(filter);
                    Task t = tas.ContinueWith(page => AfterSearchRows(page.Result, side));
                    return t;
                }
                else
                {
                    return RefreshCustom();
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            return Task.CompletedTask;
        }


        private Task RefreshCustom()
        {
            Task tas = RefreshGrid_();
            tas = tas.ContinueWith(t => Task.Run(() =>
            {
                DxGridRef.SetParametersAsync(ParameterView.FromDictionary(new Dictionary<string,object>() { { "SelectedDataItems", SelectedDataItems } }));
                SelectedDataItemsChanged(SelectedDataItems);
            }));
            return tas;
        }

        private async Task AfterSearchRows(BrowserDataPage<GridItem> page, string side)
        {
            try
            {
                if (page != null)
                {
                    List<object> neIten = SelectedDataItems.ToList();
                    foreach (var item in page.Items)
                    {
                        item.Side = side;
                        GetManualyDatas().Add(item);
                        neIten.Add(item);
                    }
                    SelectedDataItems = neIten;
                }
                AppState.HideLoadingStatus();

                if (!StateReadyFirst_)
                {
                    StateReadyFirst_ = true;
                }                
                await RefreshCustom(); 
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        protected override Task<BrowserDataPage<object[]>> CustomSearchRows(BrowserDataFilter filter)
        {
            if (!CanRefreshPartialGrid)
            {
                return Service.SearchRows(filter);
            }
            else
            {
                CanRefreshPartialGrid = false;
                return Service.SearchRowsPartial(filter);
                
            }
        }

        protected override Task<BrowserDataPage<P>> CustomSearchRows<P>(BrowserDataFilter filter)
        {
            if (!CanRefreshPartialGrid)
            {
                return Service.SearchRows<P>(filter);
            }
            else
            {
                CanRefreshPartialGrid = false;
                return Service.SearchRowsPartial<P>(filter);
            }
        }

        public  Task RefreshPartialGrid()
        {
            CanRefreshPartialGrid = true;
            return RefreshGrid();            
        }



        #endregion

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender && SelectionManualyDatas_ != null && SelectionManualyDatas_.Count > 0)
            {
                StateReadyFirst(true);
                DxGridRef.SelectDataItems(SelectionManualyDatas_);
            }
            return base.OnAfterRenderAsync(firstRender);
        }
        public ObservableCollection<GridItem> SelectedItems_ => new ObservableCollection<GridItem>(SelectedDataItems.Cast<GridItem>().ToList());
    }
}
