using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Projects
{
   public class SimpleArchive: Nameable
	{
		public static int ARCHIVE_MAX_COUNT = 10;

		public string FileName { get; set; }
		public string ProjectCode { get; set; }
		public string Description { get; set; }
		public string UserName { get; set; }

		public string CreationDate { get; set; }

		public int ArchiveMaxCount { get; set; }
		public string locale { get; set; }
		public long? ProjectId { get; set; }
		public long? ClientId { get; set; }

        [JsonIgnore]
        public DateTime CreationDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(CreationDate);
                }
                catch (Exception)
                {
                    return DateTime.Now;
                }
            }
        }
    }
}
