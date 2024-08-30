using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class Range
    {

        public Sheet Sheet { get; set; }
        public ObservableCollection<RangeItem> Items { get; set; }

        public Range() 
        {
            this.Items = new ObservableCollection<RangeItem>();
        }

        public Range(Sheet sheet) : this()
        {
            this.Sheet = sheet;
        }

        #region Methos

        /// <summary>
        /// Retoune le nombre total de cellules présentes dans la collection de plage.
        /// Ce nombre est égale à la somme des cellules de chaque plage.
        /// </summary>
        /// 
        [JsonIgnore]
        public int CellCount
        {
            get
            {
                int count = 0;
                foreach (RangeItem item in Items) count += item.CellCount;
                return count;
            }
        }

        /// <summary>
        /// La liste des cellules
        /// </summary>
        [JsonIgnore]
        public List<Cell> Cells
        {
            get
            {
                List<Cell> cells = new List<Cell>(0);
                foreach (RangeItem item in Items) cells.AddRange(item.Cells);
                return cells;
            }
        }

        public Cell GetFirstCell()
        {
            if (Items == null || Items.Count == 0) return null;
            return Items[0].GetFirstCell();
        }

        public Cell GetLastCell()
        {
            if (Items == null || Items.Count == 0) return null;

            return Items[Items.Count - 1].GetLastCell();
        }

        /// <summary>
        /// Retoune le nom de la plage.
        /// Ce nom est construit sur la base des noms de cellules de chaque plage.
        /// </summary>
        [JsonIgnore]
        public string Name
        {
            get
            {
                String name = "";
                foreach (RangeItem item in Items)
                {
                    name += !string.IsNullOrEmpty(name) ? ";" : "";
                    name += item.Name;
                }
                return name;
            }
        }

        /// <summary>
        /// Retoune le nom complet de la plage.
        /// Ce nom est construit en rajoutant le nom de la feuille au nom du range.
        /// </summary>
        [JsonIgnore]
        public string FullName
        {
            get
            {
                String name = this.Sheet != null && !string.IsNullOrEmpty(this.Sheet.Name) ? this.Sheet.Name + "!" : "";
                name += Name;
                return name;
            }
        }

        /// <summary>
        /// Détermine si la cellule identifiée par les coordonnées [row, col]
        /// appartient à la collection de plages.
        /// </summary>
        /// <param name="row">La numéro de ligne de la cellule</param>
        /// <param name="col">La numéro de colonne de la cellule</param>
        /// <returns>True si la cellule appartient à la collection de plages, sinon retoune False</returns>
        public bool Contains(int row, int col, int sheet)
        {
            foreach (RangeItem item in Items)
            {
                if (item.Contains(row, col, sheet)) return true;
            }
            return false;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns>Le nom complet du Range</returns>
        public override string ToString()
        {
            return FullName;
        }

        //public void PerformCellAction(Action<Cell> action)
        //{
        //    foreach (RangeItem item in Items)
        //    {
        //        item.PerformCellAction(action);
        //    }
        //}

        //public void PerformCellAction(CellAction action)
        //{
        //    foreach (RangeItem item in Items)
        //    {
        //        item.PerformCellAction(action);
        //    }
        //}

        #endregion


    }
}
