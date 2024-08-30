using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Bcephal.Models.Routines;
using Bcephal.Models.Utils;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    [Serializable]
    public class Join: SchedulableObject
    {
        public bool ShowAllRowsByDefault { get; set; }

        public bool AllowLineCounting { get; set; }

        public bool Consolidated { get; set; }

        public bool RefreshGridsBeforePublication { get; set; }

        public bool AddPublicationRunNbr { get; set; }

        public long? PublicationRunAttributeId { get; set; }

        public long? PublicationRunSequenceId { get; set; }

        public string PublicationMethod { get; set; }

        [JsonIgnore]
        public JoinPublicationMethod JoinPublicationMethod
        {
            get
            {
                return string.IsNullOrEmpty(PublicationMethod) ? JoinPublicationMethod.NEW_GRID : JoinPublicationMethod.GetByCode(PublicationMethod);
            }
            set
            {
                this.PublicationMethod = value != null ? value.code : null;
            }
        }


        public long? PublicationGridId { get; set; }

        public string PublicationGridName { get; set; }

        public UniverseFilter Filter { get; set; }

        public UniverseFilter AdminFilter { get; set; }


        public ListChangeHandler<JoinGrid> GridListChangeHandler { get; set; }

        public ListChangeHandler<JoinKey> KeyListChangeHandler { get; set; }

        public ListChangeHandler<JoinColumn> ColumnListChangeHandler { get; set; }

        public ListChangeHandler<JoinCondition> ConditionListChangeHandler { get; set; }

        public ListChangeHandler<RoutineExecutor> RoutineListChangeHandler;

        #region Constructors
        public Join()
        {
            this.GridListChangeHandler = new ListChangeHandler<JoinGrid>();
            this.KeyListChangeHandler = new ListChangeHandler<JoinKey>();
            this.ColumnListChangeHandler = new ListChangeHandler<JoinColumn>();
            this.ConditionListChangeHandler = new ListChangeHandler<JoinCondition>();
            this.RoutineListChangeHandler = new ListChangeHandler<RoutineExecutor>();
            this.JoinPublicationMethod = JoinPublicationMethod.NEW_GRID;
            this.VisibleInShortcut = true;
            this.ShowAllRowsByDefault = false;
            this.AllowLineCounting = true;
        }
        #endregion

        #region JoinGrid Operations
        public void AddGrid(JoinGrid item, bool sort = true)
        {
            item.Position = GridListChangeHandler.Items.Count;
            GridListChangeHandler.AddNew(item, sort);
        }

        public void UpdateGrid(JoinGrid item, bool sort = true)
        {
            GridListChangeHandler.AddUpdated(item, sort);
        }


        public void DeleteOrForgetGrid(JoinGrid item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                DeleteGrid(item, sort);
            }
            else
            {
                ForgetGrid(item, sort);
            }
        }

        public void DeleteGrid(JoinGrid item, bool sort = true)
        {
            GridListChangeHandler.AddDeleted(item, sort);
            foreach (JoinGrid child in GridListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    GridListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetGrid(JoinGrid item, bool sort = true)
        {
            GridListChangeHandler.forget(item, sort);
            foreach (JoinGrid child in GridListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    GridListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public JoinGrid GetItemByGrid(int? gridOid)
        {
            foreach (JoinGrid item in GridListChangeHandler.Items)
            {
                if (item.GridId.Equals(gridOid)) return item;
            }
            return null;
        }
        #endregion

        #region JoinKey Operations
        public void AddKey(JoinKey key, bool sort = true)
        {
            key.Position = KeyListChangeHandler.Items.Count;
            KeyListChangeHandler.AddNew(key, sort);
        }

        public void UpdateKey(JoinKey key, bool sort = true)
        {
            KeyListChangeHandler.AddUpdated(key, sort);
        }

        public void DeleteOrForgetKey(JoinKey key, bool sort = true)
        {
            if (key.IsPersistent)
            {
                DeleteKey(key, sort);
            }
            else
            {
                ForgetKey(key, sort);
            }
        }

        public void DeleteKey(JoinKey key, bool sort = true)
        {
            KeyListChangeHandler.AddDeleted(key, sort);
            foreach (JoinKey child in KeyListChangeHandler.Items)
            {
                if (child.Position > key.Position)
                {
                    child.Position = child.Position - 1;
                    KeyListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetKey(JoinKey key, bool sort = true)
        {
            KeyListChangeHandler.forget(key, sort);
            foreach (JoinKey child in KeyListChangeHandler.Items)
            {
                if (child.Position > key.Position)
                {
                    child.Position = child.Position - 1;
                    KeyListChangeHandler.AddUpdated(child, false);
                }
            }
        }
        #endregion

        #region JoinColumn Operations
        public void AddColumn(JoinColumn column, bool sort = true)
        {
            column.Position = ColumnListChangeHandler.Items.Count;
            ColumnListChangeHandler.AddNew(column, sort);
        }

        public void UpdateColumn(JoinColumn column, bool sort = true)
        {
            ColumnListChangeHandler.AddUpdated(column, sort);
        }


        public void DeleteOrForgetColumn(JoinColumn column, bool sort = true)
        {
            if (column.IsPersistent)
            {
                DeleteColumn(column, sort);
            }
            else
            {
                ForgetColumn(column, sort);
            }
        }

        public void DeleteColumn(JoinColumn column, bool sort = true)
        {
            ColumnListChangeHandler.AddDeleted(column, sort);
            foreach (JoinColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetColumn(JoinColumn column, bool sort = true)
        {
            ColumnListChangeHandler.forget(column, sort);
            foreach (JoinColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public JoinColumn GetColumnByColumnOid(int? columnId)
        {
            foreach (JoinColumn column in ColumnListChangeHandler.Items)
            {
                if (column.ColumnId.Equals(columnId)) return column;
            }
            return null;
        }

        public JoinColumn GetColumn(string col)
        {
            foreach (JoinColumn column in ColumnListChangeHandler.Items)
            {
                if (column.Name.Equals(col)) return column;
            }
            return null;
        }

        public JoinColumn GetColumnUsing(JoinColumn column)
        {
            foreach (JoinColumn col in ColumnListChangeHandler.Items)
            {
                //if (col.IsCustom && (col.Column == column || (col.columnOid.HasValue && col.columnOid.Value.Equals(column.oid)))
                //{
                //    return column;
                //}
            }
            return null;
        }

        public string GetNewColumnName()
        {
            string basename = "Column ";
            int count = 1;
            string name = basename + count++;
            while (GetColumn(name) != null)
            {
                name = basename + count++;
            }
            return name;
        }

        public List<int> GetPeriodColumnPositions()
        {
            List<int> positions = new List<int>(0);
            foreach (JoinColumn col in this.ColumnListChangeHandler.Items)
            {
                if (col.Type == DimensionType.PERIOD)
                {
                    positions.Add(col.Position);
                }
            }
            return positions;
        }
        #endregion

        #region JoinCondition Operations
        public void AddCondition(JoinCondition condition, bool sort = true)
        {
            condition.Position = ConditionListChangeHandler.Items.Count;
            ConditionListChangeHandler.AddNew(condition, sort);
        }

        public void UpdateCondition(JoinCondition condition, bool sort = true)
        {
            ConditionListChangeHandler.AddUpdated(condition, sort);
        }


        public void DeleteOrForgetCondition(JoinCondition condition, bool sort = true)
        {
            if (condition.IsPersistent)
            {
                DeleteCondition(condition, sort);
            }
            else
            {
                ForgetCondition(condition, sort);
            }
        }

        public void DeleteCondition(JoinCondition condition, bool sort = true)
        {
            ConditionListChangeHandler.AddDeleted(condition, sort);
            foreach (JoinCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetCondition(JoinCondition condition, bool sort = true)
        {
            ConditionListChangeHandler.forget(condition, sort);
            foreach (JoinCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public JoinCondition GetConditionUsing(JoinCondition column)
        {
            return null;
        }
        #endregion

        #region RoutineExecutor Operations
        public void AddRoutine(RoutineExecutor condition, bool sort = true)
        {
            condition.Position = RoutineListChangeHandler.Items.Count;
            RoutineListChangeHandler.AddNew(condition, sort);
        }

        public void UpdateRoutine(RoutineExecutor routine, bool sort = true)
        {
            RoutineListChangeHandler.AddUpdated(routine, sort);
        }


        public void DeleteOrForgetRoutine(RoutineExecutor condition, bool sort = true)
        {
            if (condition.IsPersistent)
            {
                DeleteRoutine(condition, sort);
            }
            else
            {
                ForgetRoutine(condition, sort);
            }
        }

        public void DeleteRoutine(RoutineExecutor routine, bool sort = true)
        {
            RoutineListChangeHandler.AddDeleted(routine, sort);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetRoutine(RoutineExecutor routine, bool sort = true)
        {
            RoutineListChangeHandler.forget(routine, sort);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }
        #endregion

        public void reverseColumn(int previewPosition, int NewPosition, JoinGrid joinGrid)
        {
            bool isUp = previewPosition > NewPosition;
            ObservableCollection<JoinGrid> its_ = GridListChangeHandler.Items;
            its_.BubbleSort();
            IList<JoinGrid> its = its_;
            if (NewPosition >= 0 && NewPosition < its.Count && previewPosition >= 0 && previewPosition < its.Count)
            {
                if (isUp)
                {
                    JoinGrid child = its[NewPosition]; 
                    joinGrid.Position = NewPosition;
                    GridListChangeHandler.AddUpdated(joinGrid, false);
                    its[NewPosition] = joinGrid;
                    int position_ = previewPosition;
                    while (position_ - 1 > NewPosition)
                    {
                        its[position_] = its[position_ - 1];
                        its[position_].Position = position_;
                        GridListChangeHandler.AddUpdated(its[position_], false);
                        position_--;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    GridListChangeHandler.AddUpdated(child, false);
                }
                else
                {
                    JoinGrid child = its[NewPosition];
                    joinGrid.Position = NewPosition;
                    GridListChangeHandler.AddUpdated(joinGrid, false);
                    its[NewPosition] = joinGrid;
                    int position_ = previewPosition;
                    while (position_ + 1 < NewPosition)
                    {
                        its[position_] = its[position_ + 1];
                        its[position_].Position = position_;
                        GridListChangeHandler.AddUpdated(its[position_], false);
                        position_++;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    GridListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void reverseColumn(int previewPosition, int NewPosition, JoinColumn joinColumn)
        {
            bool isUp = previewPosition > NewPosition;
            ObservableCollection<JoinColumn> its_ = ColumnListChangeHandler.Items;
            its_.BubbleSort();
            IList<JoinColumn> its = its_;
            if (NewPosition >= 0 && NewPosition < its.Count && previewPosition >= 0 && previewPosition < its.Count)
            {
                if (isUp)
                {
                    JoinColumn child = its[NewPosition];
                    joinColumn.Position = NewPosition;
                    ColumnListChangeHandler.AddUpdated(joinColumn, false);
                    its[NewPosition] = joinColumn;
                    int position_ = previewPosition;
                    while (position_ - 1 > NewPosition)
                    {
                        its[position_] = its[position_ - 1];
                        its[position_].Position = position_;
                        ColumnListChangeHandler.AddUpdated(its[position_], false);
                        position_--;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
                else
                {
                    JoinColumn child = its[NewPosition];
                    joinColumn.Position = NewPosition;
                    ColumnListChangeHandler.AddUpdated(joinColumn, false);
                    its[NewPosition] = joinColumn;
                    int position_ = previewPosition;
                    while (position_ + 1 < NewPosition)
                    {
                        its[position_] = its[position_ + 1];
                        its[position_].Position = position_;
                        ColumnListChangeHandler.AddUpdated(its[position_], false);
                        position_++;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }
    }
}
