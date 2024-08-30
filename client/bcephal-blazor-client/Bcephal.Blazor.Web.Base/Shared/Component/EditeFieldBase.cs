using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Rendering;
using Microsoft.AspNetCore.Components.CompilerServices;
using Microsoft.AspNetCore.Components.Web;
using System;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    
    public class EditeFieldBase<T> : ComponentBase     where T : Nameable
    {
        [Parameter]
        public T Item { get; set; }
        [Parameter]
        public EventCallback<T> ItemChanged { get; set; }

        public T Binditem
        {
            get { return Item; }
            set
            {
                Item = value;
                ItemChanged.InvokeAsync(value);
            }
        }

        [Parameter]
        public EventCallback<T> AddOrUpdateItemCallBack { get; set; }

        [Parameter]
        public EventCallback<T> DeleteItemCallBack { get; set; }
        

        [Parameter]
        public string NullText { get; set; }

        public void EndEditionFocusOut(FocusEventArgs args)
        {
            Console.WriteLine("FocusEventArgs :   " + args);
        }

        public void DeleteItem(MouseEventArgs args)
        {
            Console.WriteLine("call DeleteItem on base :   " + args);
            DeleteItemCallBack.InvokeAsync(Item);
        }

        protected override void BuildRenderTree(RenderTreeBuilder __builder)
        {
            int index = 0;
            __builder.OpenComponent<DxGridLayout>(index);
            __builder.AddAttribute(index, "Rows", (RenderFragment)((__builder2) => {
                __builder2.AddMarkupContent(index, "\r\n");
                __builder2.OpenComponent<DxGridLayoutRow>(index);
                __builder2.AddAttribute(index, "Height", "50px");
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(index, "\r\n");
            }
            ));
            __builder.AddAttribute(index, "Columns", (RenderFragment)((__builder2) => {
                __builder2.AddMarkupContent(index, "\r\n");
                __builder2.OpenComponent<DxGridLayoutColumn>(index);
                __builder2.AddAttribute(index, "Width", "100%");
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(index, "\r\n");
            }
            ));
            __builder.AddAttribute(index, "Items", (RenderFragment)((__builder2) => {
                __builder2.AddMarkupContent(index, "\r\n");
                __builder2.AddContent(index, "                    ");
                __builder2.OpenComponent<DxGridLayoutItem>(index);
                __builder2.AddAttribute(index, "Row", RuntimeHelpers.TypeCheck<System.Int32>(0));
                __builder2.AddAttribute(index, "Column", RuntimeHelpers.TypeCheck<System.Int32>(0));
                        __builder2.AddAttribute(index, "Template", (RenderFragment)((__builder3) => {
                            __builder3.AddMarkupContent(index, "\r\n");
                            __builder3.OpenElement(index, "div");
                            __builder3.AddMarkupContent(index, "\r\n");
                            __builder3.AddContent(index, (__builder4) => {
                                __builder4.AddMarkupContent(index, "\r\n");
                                __builder4.OpenComponent<DxTextBox>(index);
                                __builder4.AddAttribute(index, "Text", Item != null && !string.IsNullOrEmpty(Item.Name) ? Item.Name : "");
                                __builder4.AddAttribute(index, "BindValueMode", BindValueMode.OnLostFocus);
                                __builder4.AddAttribute(index, "onfocusout", EventCallback.Factory.Create<FocusEventArgs>(this, EndEditionFocusOut));
                                __builder4.AddAttribute(index, "NullText", NullText);
                                __builder4.CloseComponent();
                                __builder4.AddMarkupContent(index, "\r\n");
                            }
                        );
                            __builder3.AddMarkupContent(index, "\r\n");
                            __builder3.CloseElement();
                            __builder3.AddMarkupContent(index, "\r\n");
                        }
                        ));
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(index, "\r\n");

                var DeleteIteCallBack = EventCallback.Factory.Create<MouseEventArgs>(this, DeleteItem);

                __builder2.AddMarkupContent(index, "\r\n");
                __builder2.AddContent(index, "                    ");
                __builder2.OpenComponent<DxGridLayoutItem>(index);
                __builder2.AddAttribute(index, "Row", RuntimeHelpers.TypeCheck<System.Int32>(0));
                __builder2.AddAttribute(index, "Column", RuntimeHelpers.TypeCheck<System.Int32>(1));
                __builder2.AddAttribute(index, "Template", (RenderFragment)((__builder3) => {
                    __builder3.AddMarkupContent(index, "\r\n");
                    __builder3.AddContent(index, "                    ");
                    __builder3.OpenComponent<DxButton>(index);
                    __builder3.AddAttribute(index, "IconCssClass", "ml-1 bi-file-x");
                    __builder3.AddAttribute(index, "Click", DeleteIteCallBack);
                    __builder3.AddMarkupContent(index, "\r\n");
                    __builder3.CloseComponent();
                    __builder3.AddMarkupContent(index, "\r\n");
                }
                ));
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(index, "\r\n");
            }
            ));
            __builder.CloseComponent();
        }
    }
}
