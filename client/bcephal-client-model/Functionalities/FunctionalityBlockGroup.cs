using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Functionalities
{
    public class FunctionalityBlockGroup
    {
        public long? Id { get; set; }

        public long? ProjectId { get; set; }

        public string Name { get; set; }

        public string Username { get; set; }

        public int Position { get; set; }

        public int? Background { get; set; }

        public int? Foreground { get; set; }

        public bool defaultGroup { get; set; }

        public ListChangeHandler<FunctionalityBlock> BlockListChangeHandler { get; set; }

        public FunctionalityBlockGroup()
        {
            this.BlockListChangeHandler = new ListChangeHandler<FunctionalityBlock>();
        }

        public void AddBlock(FunctionalityBlock block, bool sort = true)
        {
            block.Position = BlockListChangeHandler.Items.Count;
            BlockListChangeHandler.AddNew(block, sort);
        }

        public void UpdateBlock(FunctionalityBlock block, bool sort = true)
        {
            BlockListChangeHandler.AddUpdated(block, sort);
        }

        public void InsertBlock(int position, FunctionalityBlock block)
        {
            block.Position = position;
            foreach (FunctionalityBlock child in BlockListChangeHandler.Items)
            {
                if (child.Position >= block.Position)
                {
                    child.Position = child.Position + 1;
                    BlockListChangeHandler.AddUpdated(child, false);
                }
            }
            BlockListChangeHandler.AddNew(block);
        }


        public void DeleteOrForgetBlock(FunctionalityBlock block)
        {
            if (block.Id.HasValue)
            {
                DeleteBlock(block);
            }
            else
            {
                ForgetBlock(block);
            }
        }

        public void DeleteBlock(FunctionalityBlock block)
        {
            BlockListChangeHandler.AddDeleted(block);
            foreach (FunctionalityBlock child in BlockListChangeHandler.Items)
            {
                if (child.Position > block.Position)
                {
                    child.Position = child.Position - 1;
                    BlockListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetBlock(FunctionalityBlock block)
        {
            BlockListChangeHandler.forget(block);
            foreach (FunctionalityBlock child in BlockListChangeHandler.Items)
            {
                if (child.Position > block.Position)
                {
                    child.Position = child.Position - 1;
                    BlockListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public FunctionalityBlock GetBlockByName(string name)
        {
            foreach (FunctionalityBlock block in BlockListChangeHandler.Items)
            {
                if (block.Name.Equals(name))
                {
                    return block;
                }
            }
            return null;
        }
        [JsonIgnore]
        public string Key
        {
            get
            {
                if (string.IsNullOrWhiteSpace(Key_))
                {
                    Key_ = Guid.NewGuid().ToString("d");
                }
                return Key_;
            }
            set
            {
                Key_ = value;
            }
        }

        [JsonIgnore]
        private string Key_ { get; set; }

    }
}
