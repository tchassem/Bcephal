using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services.Mocks
{
    public class ChemicalElementGroup
    {
        Lazy<List<ChemicalElementGroup>> groups = new Lazy<List<ChemicalElementGroup>>();

        public ChemicalElementGroup(string name, List<ChemicalElementGroup> groups = null)
        {
            Name = name;
            if (groups != null)
                Groups.AddRange(groups);
        }

        public string Name { get; set; }

        public List<ChemicalElementGroup> Groups { get { return groups.Value; } }
    }

    public static class ChemicalElements
    {
        private static readonly Lazy<List<ChemicalElementGroup>> chemicalElementGroups = new Lazy<List<ChemicalElementGroup>>(() => {
            List<ChemicalElementGroup> groups = new List<ChemicalElementGroup>() {
      new ChemicalElementGroup("Metals", new List<ChemicalElementGroup>() {
        new ChemicalElementGroup("Alkali metals"),
        new ChemicalElementGroup("Inner transition elements", new List<ChemicalElementGroup>() {
          new ChemicalElementGroup("Lanthanides"),
          new ChemicalElementGroup("Actinides")
        }),
      }),
      new ChemicalElementGroup("Nonmetals", new List<ChemicalElementGroup>() {
        new ChemicalElementGroup("Halogens"),
        new ChemicalElementGroup("Noble gases"),
      })
    };
            return groups;
        });

        public static List<ChemicalElementGroup> Groups { get { return chemicalElementGroups.Value; } }
    }
}
