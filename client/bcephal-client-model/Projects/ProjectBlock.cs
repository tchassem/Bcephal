using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace Bcephal.Models.Projects
{
    public class ProjectBlock : Nameable
    {

        public long? ProjectId { get; set; }

        public long? SubcriptionId { get; set; }

        public string Code { get; set; }

        public string Username { get; set; }

        public int Position { get; set; }

        public int? Background { get; set; }

        public int?Foreground { get; set; }

        public bool FlowBreak { get; set; }

        [JsonIgnore]
        private string Background_;
        [JsonIgnore]
        private string Foreground_;

        [JsonIgnore]
        public int DefaultIntColor = 16777215;

        [JsonIgnore]
        public string Backgrounds
        {
            get { 
                return Background.HasValue ? convertToHex(Background.Value) : Background_ != null ? Background_ : "#ffffff"; 
            }
            set
            {
                Background = Convert.ToInt32(value.Substring(1), 16);
                Background_ = convertToHex(Background.Value);
            }
        }
        [JsonIgnore]
        public string Foregrounds
        {
            get {
                return Foreground.HasValue ? convertToHex(Foreground.Value) : Foreground_ != null ? Foreground_ : "#000000"; 
            }
            set
            {
                Foreground = Convert.ToInt32(value.Substring(1), 16);
                Foreground_ = convertToHex(Foreground.Value);
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
    }
}
