using Microsoft.AspNetCore.Components;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    public class BottomNewGrid : RecoNewGrid
    {
        protected override string KeyName { get; set; } = "BottomGridRecoGridAbstractGridComponent";
        protected override string height => "calc(100% - 21px)";
        public override bool AllowBuildChecBoxTotalPage => false;
    }
}
