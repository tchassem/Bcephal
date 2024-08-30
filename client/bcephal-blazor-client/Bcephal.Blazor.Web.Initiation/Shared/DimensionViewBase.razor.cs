using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Initiation.Domain;
using Bcephal.Blazor.Web.Initiation.Services;
using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Attribute = Bcephal.Blazor.Web.Initiation.Domain.Attribute;

namespace Bcephal.Blazor.Web.Initiation.Shared
{
    public partial class DimensionViewBase<P> : Form<P, BrowserData> where P : Nameable
    {
        private static readonly string CLIPBOARD_EVENT_TYPE = "PASTE";
        private static readonly string ATTRIBUTE_KEY = "ATTRIBUTE";
        private static readonly string ENTITY_KEY = "ENTITY";
        private static readonly string MODEL_KEY = "MODEL";

        [Inject] public ClipboardService ClipboardService { get; set; }

        [Parameter] public Service<P, BrowserData> CurrenService { get; set; }

        [Parameter] public string FuncTitle { get; set; } = "";
        public string FuncItemTitle { get; set; } = "";

        public ObservableCollection<Nameable> DimensionDataSource_
        {
            get
            {
                return new (DimensionDataSource);
            }
        }

        public ObservableCollection<P> DimensionDataSource
        {
            get
            {
                ObservableCollection<P> items = null;
                if (typeof(P).Equals(typeof(Model)))
                {
                    items = ModelEditorData.AllModels.Items as ObservableCollection<P>;
                }
                else if (typeof(P).Equals(typeof(Measure)))
                {
                    items = (EditorData.Item as Measure).ChildrenListChangeHandler.Items as ObservableCollection<P>;
                }
                else
                {
                    items = (EditorData.Item as PeriodName).ChildrenListChangeHandler.Items as ObservableCollection<P>;
                }
                return new(items);
            }
        }

        protected override string DuplicateName()
        {
            return (EditorData.Item is Measure) ? AppState["duplicate.measure.name", EditorData.Item.Name] : (EditorData.Item is PeriodName) ? AppState["duplicate.periode.name", EditorData.Item.Name] : AppState["duplicate.model.and.attribute.name", EditorData.Item.Name];
        }

        public string ActionType { get; set; } = "";
        public Nameable SelectedItem { get; set; }
        public ModelEditorData ModelEditorData
        {
            get
            {
                return EditorData as ModelEditorData;
            }
            set
            {
                EditorData = value as EditorData<P>;
            }
        }

        public List<Nameable> NamesOfItems { get; set; } = new();
        public DxContextMenu ContextMenu { get; set; }
        public bool showModalItem { get; set; }
        public bool DeleteConfirmationPopup { get; set; }
        public bool CanDisplayOK { get; set; } = true;
        public string DeletionTitle { get; set; }
        public string DeleteMessage { get; set; } = "";
        public override string LeftTitlePage => null;
        private string CanDeleleKey { get; set; } = "CanDeleleKey";
        private string UnableToDeleleKey { get; set; } = "UnableToDeleleKey";
        public override string LeftTitle => typeof(P).Equals(typeof(Measure)) ? @AppState["MeasureData.Tree"] : (typeof(P).Equals(typeof(PeriodName)) ? @AppState["PeriodData.Tree"] : @AppState["edit.models"]);
        
        public string FuncNewTitle => typeof(P).Equals(typeof(Measure)) ? @AppState["MeasureData.New"] : (typeof(P).Equals(typeof(PeriodName)) ? @AppState["PeriodData.New"] : @AppState["New.model"]);

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedInitiationModel;
                var second = AppState.PrivilegeObserver.CanEditInitiationModel(EditorData.Item);
                return first || second;
            }
        }

        public bool EditableMeasure
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedInitiationMeasure;
                var second = AppState.PrivilegeObserver.CanEditInitiationMeasure(EditorData.Item);
                return first || second;
            }
        }

        public bool EditablePeriod
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedInitiationPeriod;
                var second = AppState.PrivilegeObserver.CanEditInitiationPeriod(EditorData.Item);
                return first || second;
            }
        }

        public override Task SetParametersAsync(ParameterView parameters)
        {
            usingMixPane = false;
            displayRight = DISPLAY_NONE;
            displayLeft = WIDTH_100;
            return base.SetParametersAsync(parameters);
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override Service<P, BrowserData> GetService()
        {
            return CurrenService;
        }

        bool CanCreate
        {
            get
            {
                if (typeof(P).Equals(typeof(Model)))
                {
                    return AppState.PrivilegeObserver.CanCreatedInitiationModel;
                }
                else if (typeof(P).Equals(typeof(Measure)))
                {
                    return AppState.PrivilegeObserver.CanCreatedInitiationMeasure;
                }
                else
                {
                    return AppState.PrivilegeObserver.CanCreatedInitiationPeriod;
                }
            }
        }
        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                if (CanCreate) 
                { 
                    AppState.CanCreate = true;
                    AppState.CanPaste = true;
                }               
                AppState.CreateHander += showEditDimension;
                AppState.PasteHander += PasteDimension;
                FuncItemTitle = FuncNewTitle;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanCreate = false;
            AppState.CanPaste = false;
            AppState.CreateHander -= showEditDimension;
            AppState.PasteHander -= PasteDimension;
            AppState.Update = false;
            FuncItemTitle = FuncNewTitle;
            return base.DisposeAsync();
        }

        protected override async void save()
        {
            if (EditorData.Item is not Measure && EditorData.Item is not PeriodName)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    ModelEditorData.AllModels = await GetService().SaveAll(ModelEditorData.AllModels);
                    AppState.Update = false;                    
                    ToastService.ShowSuccess(AppState["save.SuccessfullyAdd", LeftTitlePage]);
                }
                catch (Exception ex)
                {
                    AppState.Update = true;
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    StateHasChanged();
                }
            }
            else
            {
                base.save();
            }
        }

        protected override void AfterSave(EditorData<P> EditorData)
        {
            AfterInit(EditorData);
        }

        // -------------------------------------------------------------------------------------------------------------------------------------------------------------------
        public async Task OnContextMenu(MouseEventArgs e, Nameable item)
        {
            SelectedItem = item;
            await ContextMenu.ShowAsync(e);
        }

        // -------------------------------------------------------------------------------------------------------------------------------------------------------------------
        public Task SelectItem(MouseEventArgs e, Nameable item)
        {
            SelectedItem = item;
            return Task.CompletedTask;
        }

        // -------------------------------------------------------------------------------------------------------------------------------------------------------------------
        async Task OnItemClick(ContextMenuItemClickEventArgs args)
        {
            ActionType = args.ItemInfo.Name;
            FuncItemTitle = args.ItemInfo.Text;
            if (ActionType.Equals("Add") || ActionType.Equals("Edit") || ActionType.Equals("AddVal"))
            {
                showEditDimension();
            }else
            if (ActionType.Equals("Paste"))
            {
                await PasteDimension();
            }
            else if (ActionType.Equals("Delete"))
            {
                FuncItemTitle = FuncNewTitle;
                bool canDelete = !SelectedItem.Id.HasValue;
                if (SelectedItem.Id.HasValue && SelectedItem is Measure)
                {
                    canDelete = await (GetService() as MeasuresService).CanDelete(SelectedItem.Id.Value);
                }
                else if (SelectedItem.Id.HasValue && SelectedItem is PeriodName)
                {
                    canDelete = await (GetService() as PeriodService).CanDelete(SelectedItem.Id.Value);
                }
                else if (SelectedItem.Id.HasValue && SelectedItem is Attribute)
                {
                    canDelete = await (GetService() as ModelInitService).CanDelete(ATTRIBUTE_KEY, SelectedItem.Id.Value);
                }
                else if (SelectedItem.Id.HasValue && SelectedItem is Entity)
                {
                    canDelete = await (GetService() as ModelInitService).CanDelete(ENTITY_KEY, SelectedItem.Id.Value);
                }
                else if (SelectedItem.Id.HasValue && SelectedItem is Model)
                {
                    canDelete = await (GetService() as ModelInitService).CanDelete(MODEL_KEY, SelectedItem.Id.Value);
                }
                
                if (string.IsNullOrWhiteSpace(DeletionTitle))
                {
                    DeletionTitle = AppState["DeletionTitle"];
                }
                DeleteMessage = canDelete ? AppState["SureToDeleteSelectedItem", SelectedItem.Name] : AppState["UnableToDeleteSelectedItem", SelectedItem.Name];
                if (!canDelete)
                {
                    CanDisplayOK = false;
                }
                DeleteConfirmationPopup = true;
            }
        }

        private RenderFormContent RenderFormContentRef { get; set; }
        private void showEditDimension()
        {
            if (string.IsNullOrEmpty(ActionType))
            {
                SelectedItem = null;
            }
            if (!typeof(P).Equals(typeof(PeriodName)) && !typeof(P).Equals(typeof(Measure)))
            {
                FuncTitle = AppState["create.object"];
            }
            showModalItem = true;
            if(RenderFormContentRef != null)
            {
                RenderFormContentRef.StateHasChanged_();
            }
        }

        protected void CloseDimensionDialog(bool isVisible)
        {
            ActionType = "";
            SelectedItem = null;
            showModalItem = isVisible;
            FuncItemTitle = FuncNewTitle;
            if (RenderFormContentRef != null)
            {
                RenderFormContentRef.StateHasChanged_();
            }
        }

        // -------------------------------------------------- Here you find the dialog box alert message for deleting action -------------------------------------------------
        private void CancelDeletion()
        {
            DeleteConfirmationPopup = false;
            CanDisplayOK = true;
        }

        protected override void AfterInit(EditorData<P> EditorData)
        {
            if (EditorData.Item != null)
            {
                NamesOfItems.Clear();
                if(EditorData.Item is Model)
                {
                    if (ModelEditorData.AllModels != null)
                    {
                        InitParentForDimensions(ModelEditorData.AllModels.Items, EditorData.Item);
                        settingsListNames(ModelEditorData.AllModels.Items, EditorData.Item);
                    }
                }
                else if (EditorData.Item is Measure)
                {
                    InitParentForDimensions((EditorData.Item as Measure).ChildrenListChangeHandler.Items, EditorData.Item);
                    settingsListNames((EditorData.Item as Measure).ChildrenListChangeHandler.Items, EditorData.Item);
                }
                else
                {
                    InitParentForDimensions((EditorData.Item as PeriodName).ChildrenListChangeHandler.Items, EditorData.Item);
                    settingsListNames((EditorData.Item as PeriodName).ChildrenListChangeHandler.Items, EditorData.Item);
                }
            }
        }

        protected void settingsListNames(IEnumerable<Nameable> items, Nameable currentObject)
        {
            if (!string.IsNullOrEmpty(currentObject.Name))
            {
                if (!NameAlreadyExist(currentObject))
                {
                    NamesOfItems.Add(currentObject);
                }
            }

            if (items != null && items.Count() > 0)
            {
                foreach (var item in items)
                {
                    if (item is Measure)
                    {
                        settingsListNames((item as Measure).ChildrenListChangeHandler.Items, item);
                    }
                    else if (item is PeriodName)
                    {
                        settingsListNames((item as PeriodName).ChildrenListChangeHandler.Items, item);
                    }
                    else if (currentObject is Model)
                    {
                        if (item is Model)
                        {
                            settingsListNames((item as Model).EntityListChangeHandler.Items, item);
                        }
                        else
                        {
                            settingsListNames((item as Entity).AttributeListChangeHandler.Items, item);
                        }
                    }
                    else
                    {
                        settingsListNames((item as Attribute).ChildrenListChangeHandler.Items, item);
                    }
                }
            }
        }

        protected void InitParentForDimensions(IEnumerable<Nameable> items, Nameable obj)
        {
            if (items != null && items.Count() > 0)
            {
                foreach (var item in items)
                {
                    if (item is Measure)
                    {
                        (item as Measure).Parent = obj as Measure;
                        InitParentForDimensions((item as Measure).ChildrenListChangeHandler.Items, item);
                        }
                    else if (item is PeriodName)
                    {
                        (item as PeriodName).Parent = obj as PeriodName;
                        InitParentForDimensions((item as PeriodName).ChildrenListChangeHandler.Items, item);
                    }
                    else
                    {
                        if (obj is Model)
                        {
                            if (item is Model)
                            {
                                InitParentForDimensions((item as Model).EntityListChangeHandler.Items, item);
                            }
                            else
                            {
                                ((Entity)item).Model = obj as Model;
                                ((Entity)item).ParentId = ((Model)obj).Id.ToString();
                                InitParentForDimensions((item as Entity).AttributeListChangeHandler.Items, item);
                            }
                        }
                        else if (obj is Entity)
                        {
                            ((Attribute)item).Entity = obj as Entity;
                            InitParentForDimensions((item as Attribute).ChildrenListChangeHandler.Items, item);
                        }
                        else if (obj is Attribute)
                        {
                            ((Attribute)item).Parent = obj as Attribute;
                            ((Attribute)item).ParentId = ((Attribute)obj).Id.ToString();
                            InitParentForDimensions((item as Attribute).ChildrenListChangeHandler.Items, item);
                        }
                    }
                }
            }
        }

        public async Task HandlePaste(ClipboardEventArgs eventsArgs)
        {
            if (eventsArgs.Type.ToLower().Equals(CLIPBOARD_EVENT_TYPE.ToLower()))
            {
                await PasteDimension();
            }
        }

        protected async Task PasteDimension()
        {
            AppState.ShowLoadingStatus();
            string data = "";
            try
            {
                data = await ClipboardService.ReadTextAsync();
            }
            catch (Exception)
            {
                SelectedItem = null;
                AppState.HideLoadingStatus();
                await InvokeAsync(StateHasChanged); 
            }
            if (!string.IsNullOrWhiteSpace(data))
            {
                List<string> Items = data.Trim().Split("\r\n").ToList();

                ActionType = (SelectedItem != null) ? "Add" : "";

                List<Nameable> dimensions = new List<Nameable>();
                dimensions = Items.Where(name => !string.IsNullOrWhiteSpace(name))
                    .Distinct().ToList().Select(s => {
                    Nameable n = typeof(P).Equals(typeof(Measure)) ? new Measure() : (typeof(P).Equals(typeof(PeriodName)) ? new PeriodName() : 
                    (SelectedItem == null ? new Model() : (SelectedItem is Model ? new Entity() : new Attribute())));

                    if(n is Entity)
                    {
                        ((Entity)n).Name = s;
                    }
                    else if(n is Attribute)
                    {
                        ((Attribute)n).Name = s;
                    } 
                    else
                    {
                        ((P)n).Name = s;
                    }
                    return n;
                }).ToList();

                dimensions = dimensions.Where(d => NameAlreadyExist(d).Equals(false)).ToList();

                if (SelectedItem != null)
                {
                    Model itemModel = null;
                    if (SelectedItem is Measure)
                    {
                        List<Measure> mes = dimensions.Select(d => { Measure m = new(d); return m; }).ToList();
                        (SelectedItem as Measure).AddChildrens(mes);
                        SaveDimension(SelectedItem);
                    }
                    else if (SelectedItem is PeriodName)
                    {
                        List<PeriodName> pers = dimensions.Select(d => { PeriodName m = new(d); return m; }).ToList();
                        (SelectedItem as PeriodName).AddChildrens(pers);
                        SaveDimension(SelectedItem);
                    }
                    else
                    {
                        if (SelectedItem is Model)
                        {
                            List<Entity> entities = dimensions.Select(d => { Entity m = new(d); return m; }).ToList();
                            ((Model)SelectedItem).AddEntities(entities); 
                             itemModel = (Model)SelectedItem;
                        }
                        else
                        {
                            List<Attribute> attributes = dimensions.Select(d => { Attribute m = new(d); return m; }).ToList();
                            if (SelectedItem is Entity)
                            {
                                ((Entity)SelectedItem).AddAttributes(attributes);
                                itemModel = ((Entity)SelectedItem).Model;
                            }
                            else
                            {
                                ((Attribute)SelectedItem).AddChildrens(attributes);
                                itemModel = getModelFromAttribute((Attribute)SelectedItem);
                            }
                        }
                        SaveDimension(itemModel);
                    }
                    CloseDimensionDialog(false);
                }
                else
                {
                    dimensions.ForEach(d => { SaveDimension((P)d); });
                }
                AppState.HideLoadingStatus();
                await InvokeAsync(StateHasChanged);
            }           
        }

        protected void SaveDimension(Nameable dimension)
        {
            if (dimension is Measure)
            {
                settingsListNames((EditorData.Item as Measure).ChildrenListChangeHandler.Items, dimension);
            }
            else if (dimension is PeriodName)
            {
                settingsListNames((EditorData.Item as PeriodName).ChildrenListChangeHandler.Items, dimension);
            }
            else
            {
                if(ModelEditorData.AllModels != null && ModelEditorData.AllModels.Items.Any())
                {
                    settingsListNames(ModelEditorData.AllModels.Items, dimension);
                }
            }

            if (SelectedItem != null)
            {
                if (SelectedItem.Id.HasValue)
                {
                    if (dimension is Measure)
                    {
                        (dimension as Measure).ParentId = SelectedItem.Id.Value.ToString();
                    }
                    else if (dimension is PeriodName)
                    {
                        (dimension as PeriodName).ParentId = SelectedItem.Id.Value.ToString();
                    }
                }
                if (ActionType.Equals("Add") || ActionType.Equals("Edit") || ActionType.Equals("AddVal"))
                {
                    if (dimension is Measure)
                    {
                        (SelectedItem as Measure).Parent.UpdateChild(SelectedItem as Measure);
                    }
                    else if (dimension is PeriodName)
                    {
                        (SelectedItem as PeriodName).Parent.UpdateChild(SelectedItem as PeriodName);
                    }
                    else
                    {
                        ModelEditorData.AllModels.AddUpdated((Model)dimension);
                    }
                    AppState.Update = true;
                }
            }
            else
            {
                if (dimension is Measure)
                {
                    (dimension as Measure).Parent = null;
                    (EditorData.Item as Measure).AddChild(dimension as Measure);
                }
                else if (dimension is PeriodName)
                {
                    (dimension as PeriodName).Parent = null;
                    (EditorData.Item as PeriodName).AddChild(dimension as PeriodName);
                } 
                else
                {
                    ModelEditorData.AddModel((Model)dimension);
                }
                AppState.Update = true;
            }
        }   

        protected async Task DeleteAction()
        {
            if (SelectedItem is Measure)
            {
                if ((SelectedItem as Measure).Parent != null && (SelectedItem as Measure).Parent.Id != null && !string.IsNullOrEmpty((SelectedItem as Measure).Parent.Name))
                {
                    (SelectedItem as Measure).Parent.DeleteOrForgetChild(SelectedItem as Measure);
                }
                else if ((SelectedItem as Measure).Parent.Id == null && string.IsNullOrEmpty((SelectedItem as Measure).Parent.Name))
                {
                    (EditorData.Item as Measure).DeleteOrForgetChild(SelectedItem as Measure);
                }
            }
            if (SelectedItem is PeriodName)
            {
                if ((SelectedItem as PeriodName).Parent != null && (SelectedItem as PeriodName).Parent.Id != null && !string.IsNullOrEmpty((SelectedItem as PeriodName).Parent.Name))
                {
                    (SelectedItem as PeriodName).Parent.DeleteOrForgetChild(SelectedItem as PeriodName);
                }
                else if ((SelectedItem as PeriodName).Parent.Id == null && string.IsNullOrEmpty((SelectedItem as PeriodName).Parent.Name))
                {
                    (EditorData.Item as PeriodName).DeleteOrForgetChild(SelectedItem as PeriodName);
                }
            }
            if (SelectedItem is Model)
            {
                ModelEditorData.DeleteOrForgetModel(SelectedItem as Model);
            }
            if (SelectedItem is Entity)
            {
                (SelectedItem as Entity).Model.DeleteOrForgetEntity(SelectedItem as Entity);
                ModelEditorData.UpdateModel((SelectedItem as Entity).Model);
            }
            if (SelectedItem is Attribute)
            {
                Model attrModel = getModelFromAttribute((Attribute)SelectedItem);
                if ((SelectedItem as Attribute).Parent != null)
                {
                    (SelectedItem as Attribute).Parent.DeleteOrForgetChildren(SelectedItem as Attribute);
                }
                else if((SelectedItem as Attribute).Entity != null)
                {
                    (SelectedItem as Attribute).Entity.DeleteOrForgetAttribute(SelectedItem as Attribute);
                }
                ModelEditorData.UpdateModel(attrModel);
            }
            if (SelectedItem is AttributeValue)
            {
                (SelectedItem as AttributeValue).DeleteOrForgetChildren(SelectedItem as AttributeValue);
            }
           
            NamesOfItems.Remove(SelectedItem);
            SelectedItem = null;
            DeleteConfirmationPopup = false;
            AppState.Update = true;
            await InvokeAsync(StateHasChanged);
        }

        private Model getModelFromAttribute(Attribute item)
        {
            if (item.Entity != null)
            {
                return item.Entity.Model;
            }
            else
            {
                return getModelFromAttribute(item.Parent);
            }
        }

        private bool NameAlreadyExist(Nameable item)
        {
            bool isPresent = false;
            if(NamesOfItems.Count == 0)
            {
                return isPresent;
            }
            foreach (Nameable m in NamesOfItems)
            {
                if (m.Name.Equals(item.Name) && m.GetType() == item.GetType())
                {
                    isPresent = true;
                    break;
                }
            }
            return isPresent;
        }
    }
}
