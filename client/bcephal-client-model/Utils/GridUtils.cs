using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Utils
{
    public class GridUtils
    {
        public static bool CheckIfColumnExist(Models.Grids.Grille grille, string name, Bcephal.Models.Filters.DimensionType type, Action<string> existCallback)
        {
            if (grille == null)
            {
                return false;
            }

            ObservableCollection<GrilleColumn> Items = grille.ColumnListChangeHandler.GetItems();
            if (Items.Count == 0)
            {
                return true;
            }
            foreach (var item in Items)
            {
                if (item.Type.Equals(type))
                {
                    if (!string.IsNullOrEmpty(item.Name) && !string.IsNullOrEmpty(name))
                    {
                        if (item.Name.ToLower().Equals(name.ToLower()))
                        {
                            existCallback?.Invoke(name);
                            return false;
                        }
                    }
                }
            }
            
            return true;
        }

        public static bool CheckIfDimensionExist(Models.Grids.Grille grille, string name, Bcephal.Models.Filters.DimensionType type, Action<string> existCallback)
        {
            if (grille == null)
            {
                return false;
            }

            ObservableCollection<GrilleDimension> Items = grille.DimensionListChangeHandler.GetItems();
            if (Items.Count == 0)
            {
                return true;
            }
            foreach (var item in Items)
            {
                if (item.Type.Equals(type))
                {
                    if (!string.IsNullOrEmpty(item.Name) && !string.IsNullOrEmpty(name))
                    {
                        if (item.Name.ToLower().Equals(name.ToLower()))
                        {
                            existCallback?.Invoke(name);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
}
