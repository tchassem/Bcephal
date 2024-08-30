using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.AbstractComponent
{
    public partial class GridColumnFilterComponent
    {
        [Inject]
        protected AppState AppState { get; set; }
        [Parameter] public ColumnFilter ColumnFilter { get; set; }
        [Parameter] public EventCallback<ColumnFilter> ColumnFilterChanged { get; set; }
        [Parameter] public Type ColumnType { get; set; }
        public string ColumnFormat { get; set; }

        [Parameter] public Func<Task> EnterAction { get; set; }

        private async Task EnterAction_(KeyboardEventArgs args)
        {
            await Task.Yield();
            if (args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Contains("Enter"))
            {
                await EnterAction.Invoke();
            }
        }

        private ObservableCollection<string> AttributeOperatorCollection_ { get => attributeOperator.GetAll(text => AppState[text]); }

        private ObservableCollection<string> MeasureOperatorCollection { get => MeasureOperator.GetAll() ;}

        private bool IsInit { get; set; } = false;

        private bool ShouldRender_ { get; set; } = true;
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }
        RenderFormContent RenderFormContentFilter { get; set; }
        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            
            if (firstRender)
            {
              var value =  Operation;
              IsInit = true;
                if (RenderFormContentFilter != null)
                {
                    RenderFormContentFilter.StateHasChanged_();
                }
            }
            ShouldRender_ = false;
            return base.OnAfterRenderAsync(firstRender);
        }

        private string Operation_;


        private string Operation
        {
            get
            {
                if (ColumnFilter == null)
                {
                    return null;
                }
                if (!string.IsNullOrWhiteSpace(ColumnFilter.Operation))
                {
                    Operation_ = ColumnFilter.Operation;
                }
                else
                {
                    if (ColumnType.Equals(typeof(bool)))
                    {
                        if (Operation_ == null)
                        {
                            Operation = AttributeOperator.EQUALS.ToString();
                        }
                    }else
                    if (ColumnType.Equals(typeof(string)))
                    {
                        if (Operation_ == null)
                        {
                            Operation = AttributeOperator.STARTS_WITH.ToString();
                        }
                    }
                    else if(ColumnType.Equals(typeof(DateTime?)))
                    {  
                        if(Operation_ == null)
                        {
                            ColumnFormat = "dd/MM/yyyy";
                            Operation = MeasureOperator.EQUALS;
                            ColumnFilter.dimensionType = DimensionType.PERIOD;
                        }
                    }
                    else if (ColumnType.Equals(typeof(decimal)))
                    {
                        if (Operation_ == null)
                        {
                            Operation = MeasureOperator.EQUALS;
                            ColumnFilter.dimensionType = DimensionType.MEASURE;
                        }
                    }
                }
                return Operation_;
            }
            set
            {
                ShouldRender_ = true;
                Operation_ = value;
                if (ColumnType.Equals(typeof(decimal)))
                {
                    ColumnFilter.Operation = value;
                }else
                if (ColumnType.Equals(typeof(DateTime?)))
                {
                    ColumnFilter.Operation = value;
                }else
                if (ColumnType.Equals(typeof(bool)))
                {
                    ColumnFilter.Operation = value;
                }
                else
                {
                    ColumnFilter.Operation = value != null ? value.Replace("_", "") : null;
                }
                if (ColumnFilter.Operation.Equals("NULL"))
                {
                    ColumnFilter.Operation = "IsNullOrEmpty";
                }else
                if (ColumnFilter.Operation.Equals("NOTNULL"))
                {
                    ColumnFilter.Operation = "NotIsNullOrEmpty";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.EQUALS))
                {
                    ColumnFilter.Operation = "Equals";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.GRETTER_OR_EQUALS))
                {
                    ColumnFilter.Operation = "GreaterOrEqual";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.LESS_OR_EQUALS))
                {
                    ColumnFilter.Operation = "LessOrEqual";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.LESS_THAN))
                {
                    ColumnFilter.Operation = "Less";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.GRETTER_THAN))
                {
                    ColumnFilter.Operation = "Greater";
                }
                else
                if (ColumnFilter.Operation.Equals(MeasureOperator.NOT_EQUALS))
                {
                    ColumnFilter.Operation = "NotEquals";
                }

                if (IsInit)
                {
                    ColumnFilterChanged.InvokeAsync(ColumnFilter);
                }
            }
        }

        private decimal? ValueDecimal
        { 
            get {
                if (ColumnFilter == null || string.IsNullOrWhiteSpace(ColumnFilter.Value))
                {
                    return null;
                }
                try
                {
                    decimal.TryParse(ColumnFilter.Value, out decimal val);
                    return val;
                }
                catch { }
                return null;
            } 
            set {
                ShouldRender_ = true;
                ColumnFilter.Value = value.HasValue ? value.Value.ToString() : null;
                ColumnFilterChanged.InvokeAsync(ColumnFilter);
            } 
        }

        private DateTime? ValueDateTime_ { get; set; }
        private DateTime? ValueDateTime
        {
            get
            {
                if (ColumnFilter == null || string.IsNullOrWhiteSpace(ColumnFilter.Value))
                {
                    return null;
                }
                return ValueDateTime_;
            }
            set
            {
                ShouldRender_ = true;
                ValueDateTime_ = value;
                ColumnFilter.Value = value.HasValue ? value.Value.ToString(ColumnFormat, CultureInfo.CreateSpecificCulture("fr-FR")) : null;
                ColumnFilterChanged.InvokeAsync(ColumnFilter);
            }
        }

        private string Value
        {
            get
            {
                if (ColumnFilter == null)
                {
                    return null;
                }
                return ColumnFilter.Value;
            }
            set
            {
                if (ColumnType.Equals(typeof(bool)))
                {
                    if (!string.IsNullOrWhiteSpace(value))
                    {
                        ColumnFilter.Value = value.ToLower();
                        Operation = AttributeOperator.EQUALS.ToString();
                    }
                    else
                    {
                        ColumnFilter.Value = null;
                        ColumnFilterChanged.InvokeAsync(ColumnFilter);
                    }
                }
                else
                {
                    ColumnFilter.Value = value;
                    ColumnFilterChanged.InvokeAsync(ColumnFilter);
                }
            }
        }

        private void ItemClick(ToolbarItemClickEventArgs args)
        {
            ShouldRender_ = true;
            if (ColumnType.Equals(typeof(decimal)) || ColumnType.Equals(typeof(DateTime?)))
            {
                Operation = args.ItemName;
                DefaultIcon_ = MeasureOperator.GetSymbol_(args.ItemName);
            }
            else if(ColumnType.Equals(typeof(string)))
            {
                var op = attributeOperator.GetAttributeOperator(args.ItemName, text => AppState[text]);
                DefaultIcon_ = op.GetSymbol_();
                Operation = op.Value.ToString();
            }            
        }

        private string DefaultIcon_ { get; set; }
        private string DefaultIcon {
            get{
                if (string.IsNullOrWhiteSpace(DefaultIcon_) )
                {
                    DefaultIcon_ =   ColumnType.Equals(typeof(string))
                        ? attributeOperatorStartsWith.GetSymbol_()
                        : MeasureOperator.GetSymbol_(MeasureOperator.EQUALS);
                }
                return DefaultIcon_;
            }
            set
            {
                ShouldRender_ = true;
                DefaultIcon_ = value;
            }
        
        } 

        private string GetOperatorTitle(string item)
        {
            string title = "";
            if (item.Equals(MeasureOperator.EQUALS))
            {
                title = AppState["EQUALS_"];
            }
            else
               if (item.Equals(MeasureOperator.GRETTER_OR_EQUALS))
            {
                title = AppState["greaterOrEqual"];
            }
            else
               if (item.Equals(MeasureOperator.LESS_OR_EQUALS))
            {
                title = AppState["lessOrEqual"];
            }
            else
               if (item.Equals(MeasureOperator.LESS_THAN))
            {
                title = AppState["less"];
            }
            else
               if (item.Equals(MeasureOperator.GRETTER_THAN))
            {
                title = AppState["greater"];
            }
            else
               if (item.Equals(MeasureOperator.NOT_EQUALS))
            {
                title = AppState["notEquals"];
            }
            return title;
        }

        AttributeOperator? attributeOperator => AttributeOperator.NULL;

        AttributeOperator? attributeOperatorStartsWith => AttributeOperator.STARTS_WITH;

    }
}
