using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Functionalities
{
   public  class FunctionalityBlock : IPersistent
    {
		public long? Id { get; set; }

		public long? ProjectId { get; set; }

        public long? GroupId { get; set; }

        public string Code { get; set; }

        public string Name { get; set; }

        public string Username { get; set; }

        public int Position { get; set; }

        public int? Background { get; set; }

        public int? Foreground { get; set; }

        public bool FlowBreak { get; set; }

        [JsonIgnore]
        public Functionality Functionality { get; set; }


        [JsonIgnore]
        private string Background_;
        [JsonIgnore]
        private string Foreground_;

        [JsonIgnore]
        public int DefaultIntColor = 3380735;

        [JsonIgnore]
        public string Backgrounds
        {
            get
            {
                return Background.HasValue ? convertToHex(Background.Value) : Background_ != null ? Background_ : "#3395ff";
            }
            set
            {
                Console.WriteLine("ProjectBlock Background 0 ===> " + value);
                Background = Convert.ToInt32(value.Substring(1), 16);
                Background_ = convertToHex(Background.Value);
                Console.WriteLine("ProjectBlock Background_ 0 ===> " + Background_);
            }
        }
        [JsonIgnore]
        public string Foregrounds
        {
            get
            {
                return Foreground.HasValue ? convertToHex(Foreground.Value) : Foreground_ != null ? Foreground_ : "#3395ff";
            }
            set
            {
                Console.WriteLine("ProjectBlock Foreground 0 ===> " + value);
                Foreground = Convert.ToInt32(value.Substring(1), 16);
                Foreground_ = convertToHex(Foreground.Value);
                Console.WriteLine("ProjectBlock Foreground_ 0 ===> " + Foreground_);
            }
        }

        private string convertToHex(int color)
        {
            string hex = color.ToString("X");
            while (hex.Length < 6)
            {
                hex = "0" + hex;
            }
            return "#" + hex;
        }

        public int CompareTo(object obj)
        {
            if (obj == null || !(obj is FunctionalityBlock)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((FunctionalityBlock)obj).Id)) return 0;
            if (this.Position.Equals(((FunctionalityBlock)obj).Position))
            {
                return this.Name.CompareTo(((FunctionalityBlock)obj).Name);
            }
            return this.Position.CompareTo(((FunctionalityBlock)obj).Position);
        }

        public override string ToString()
        {
            return this.Name;
        }

    }
}
