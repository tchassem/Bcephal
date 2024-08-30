using Bcephal.Models.Base;
using Bcephal.Models.Routines;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    [Serializable]
    public class FileLoader : SchedulableObject
    {

        public bool HasHeader { get; set; }

        public int HeaderRowCount { get; set; }

        public string File { get; set; }

        public bool IndentifySheetByPosition { get; set; }

        public bool LoadAllSheets { get; set; }

        public string SheetName { get; set; }

        public int SheetIndex { get; set; }

        public string Repository { get; set; }

        public string RepositoryOnServer { get; set; }

        public string FileSeparator { get; set; }

        public string FileExtension { get; set; }
        [JsonIgnore]
        public FileExtensions FileExtensions
        {
            get
            {
                return string.IsNullOrEmpty(FileExtension) ? FileExtensions.ALL : FileExtensions.GetByCode(FileExtension);
            }
            set
            {
                this.FileExtension = value != null ? value.getCode() : null;
            }
        }
        

        public string UploadMethod { get; set; }
        [JsonIgnore]
        public FileLoaderMethod FileLoaderMethod
        {
            get
            {
                return string.IsNullOrEmpty(UploadMethod) ? /*FileLoaderMethod.NEW_GRID*/ null : FileLoaderMethod.GetByCode(UploadMethod);
            }
            set
            {
                this.UploadMethod = value != null ? value.getCode() : null;
            }
        }


        public long? TargetId { get; set; }

        public string TargetName { get; set; }

        public bool AllowBackup { get; set; }

        public int MaxBackupCount { get; set; }


        public ListChangeHandler<FileLoaderColumn> ColumnListChangeHandler { get; set; }

        public ListChangeHandler<FileLoaderNameCondition> ConditionListChangeHandler { get; set; }

        public ListChangeHandler<RoutineExecutor> RoutineListChangeHandler { get; set; }


        public FileLoader()
        {
            this.ConditionListChangeHandler = new ListChangeHandler<FileLoaderNameCondition>();
            this.ColumnListChangeHandler = new ListChangeHandler<FileLoaderColumn>();
            this.RoutineListChangeHandler = new ListChangeHandler<RoutineExecutor>();
            this.Active = true;
            this.AllowBackup = true;
            this.MaxBackupCount = 5;
            this.HasHeader = false;
            this.HeaderRowCount = 1;
            this.Scheduled = false;
            //this.UploadMethod = FileLoaderMethod.NEW_GRID.ToString();
            this.UploadMethod = null;
            this.FileSeparator = ";";
        }

        public void AddCondition(FileLoaderNameCondition condition, bool sort = true)
        {
            condition.Position = ConditionListChangeHandler.Items.Count;
            ConditionListChangeHandler.AddNew(condition, sort);
        }

        public void UpdateCondition(FileLoaderNameCondition condition, bool sort = true)
        {
            ConditionListChangeHandler.AddUpdated(condition, sort);
        }

        public void DeleteOrForgetCondition(FileLoaderNameCondition condition)
        {
            if (condition.IsPersistent)
            {
                DeleteCondition(condition);
            }
            else
            {
                ForgetCondition(condition);
            }
        }

        public void DeleteCondition(FileLoaderNameCondition condition)
        {
            ConditionListChangeHandler.AddDeleted(condition);
            foreach (FileLoaderNameCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetCondition(FileLoaderNameCondition condition)
        {
            ConditionListChangeHandler.forget(condition);
            foreach (FileLoaderNameCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }




        public void AddColumn(FileLoaderColumn column, bool sort = true)
        {
            column.Position = ColumnListChangeHandler.Items.Count;
            ColumnListChangeHandler.AddNew(column, sort);
        }

        public void UpdateColumn(FileLoaderColumn column, bool sort = true)
        {
            ColumnListChangeHandler.AddUpdated(column, sort);
        }

        public void DeleteOrForgetColumn(FileLoaderColumn column)
        {
            if (column.IsPersistent)
            {
                DeleteColumn(column);
            }
            else
            {
                ForgetColumn(column);
            }
        }

        public void DeleteColumn(FileLoaderColumn column)
        {
            ColumnListChangeHandler.AddDeleted(column);
            foreach (FileLoaderColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetColumn(FileLoaderColumn column)
        {
            ColumnListChangeHandler.forget(column);
            foreach (FileLoaderColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void AddRoutine(RoutineExecutor routine, bool sort = true)
        {
            routine.Position = RoutineListChangeHandler.Items.Count;
            RoutineListChangeHandler.AddNew(routine, sort);
        }

        public void UpdateRoutine(RoutineExecutor routine, bool sort = true)
        {
            RoutineListChangeHandler.AddUpdated(routine, sort);
        }

        public void DeleteOrForgetRoutine(RoutineExecutor routine)
        {
            if (routine.IsPersistent)
            {
                DeleteRoutine(routine);
            }
            else
            {
                ForgetRoutine(routine);
            }
        }

        public void DeleteRoutine(RoutineExecutor routine)
        {
            RoutineListChangeHandler.AddDeleted(routine);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetRoutine(RoutineExecutor routine)
        {
            RoutineListChangeHandler.forget(routine);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }


    }
}
