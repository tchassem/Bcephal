using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class Model : MainObject
    {

		public int Position { get; set; }

		public string DiagramXml { get; set; }

        public bool VisibleByAdminOnly { get; set; }

        public ListChangeHandler<Entity> EntityListChangeHandler { get; set; }


		public Model()
		{
			this.EntityListChangeHandler = new ListChangeHandler<Entity>();
		}

        public void AddEntities(List<Entity> entities)
        {
            entities.ForEach(entity =>
            {
                entity.Model = this;
                EntityListChangeHandler.AddNew(entity);
            });
        }

        public void AddEntity(Entity entity)
        {
            entity.Model = this;
            EntityListChangeHandler.AddNew(entity);
        }

        public void UpdateEntity(Entity entity)
        {
            entity.Model = this;
            EntityListChangeHandler.AddUpdated(entity);
        }

        public void DeleteOrForgetEntity(Entity entity)
        {
            if (entity.IsPersistent)
            {
                DeleteEntity(entity);
            }
            else
            {
                ForgetEntity(entity);
            }            
        }

        public void DeleteEntity(Entity entity)
        {
            EntityListChangeHandler.AddDeleted(entity);
        }
               

        public void ForgetEntity(Entity entity)
        {
            EntityListChangeHandler.forget(entity);
        }

        [JsonIgnore]
        public List<Entity> Descendents
        {
            get
            {
                List<Entity> Entities = new List<Entity>();
                foreach (Entity entity in EntityListChangeHandler.Items)
                {
                    entity.Model = this;
                    Entities.Add(entity);
                }
                return Entities;
            }
        }

    }
}
