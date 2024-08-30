using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{

    public class NodeElement : Nameable
    {

        public bool IsLeaf { get; set; } = false;

        public string ItemType { get; set; }

        public List<NodeElement> Children { get; set; } = new();

    }
}
