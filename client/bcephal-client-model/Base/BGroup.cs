using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

namespace Bcephal.Models.Base
{
    [Serializable]
    public  class BGroup : MainObject
    {
        public static string DEFAULT_GROUP_NAME = "DEFAULT";
        public static string DEFAULT_GRID_ALLOCATION_GROUP_NAME = "Allocation";

        public ListChangeHandler<BGroup> childrenListChangeHandler { get; set; }

        public BGroup()
        {
            this.childrenListChangeHandler = new ListChangeHandler<BGroup>();
        }

        public BGroup(string name) : this()
        {
            this.Name = name;
        }
        public String subjectType { get; set; }


        public void AddBGroup(BGroup child)
        {
            childrenListChangeHandler.AddNew(child);
        }

        public void UpdateBGroup(BGroup child)
        {
            childrenListChangeHandler.AddUpdated(child);
        }

        public void DeleteOrForgetBGroup(BGroup entity)
        {
            if (entity.IsPersistent)
            {
                DeleteBGroup(entity);
            }
            else
            {
                ForgetBGroup(entity);
            }
        }

        public void DeleteBGroup(BGroup entity)
        {
            childrenListChangeHandler.AddDeleted(entity);
        }

        public void ForgetBGroup(BGroup child)
        {
            childrenListChangeHandler.forget(child);
        }
    }
}
