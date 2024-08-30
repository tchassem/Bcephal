using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderNameCondition : Persistent
    {

        public string Condition { get; set; }

        public string Filter { get; set; }

        public int Position { get; set; }

        [JsonIgnore]
        private string Key_ { get; set; }
        [JsonIgnore]
        public string Key {
            get {
                if (string.IsNullOrWhiteSpace(Key_))
                {
                    Key_ = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds() + "";
                }
                return Key_;
            }
            set {
                Key_ = value;
            }
        }

        [JsonIgnore]
        public FileNameCondition FileNameCondition
        {
            get { return !string.IsNullOrEmpty(Condition) ? FileNameCondition.GetByCode(Condition) : null; }
            set { this.Condition = value != null ? value.code : null; }
        }


        public bool ValidateFileName(string file)
        {
            FileNameCondition con = this.FileNameCondition;
            if (con != null)
            {
                if (con == FileNameCondition.BEGINS_WITH)
                {
                    return file.StartsWith(this.Filter, StringComparison.OrdinalIgnoreCase);
                }
                else if (con == FileNameCondition.CONTAINS)
                {
                    return file.IndexOf(this.Filter, StringComparison.OrdinalIgnoreCase) >= 0;
                }
                else if (con == FileNameCondition.DO_NOT_CONTAINS)
                {
                    return file.IndexOf(this.Filter, StringComparison.OrdinalIgnoreCase) < 0;
                }
                else if (con == FileNameCondition.ENDS_WITH)
                {
                    string name = Path.GetFileNameWithoutExtension(file);
                    return name.EndsWith(this.Filter, StringComparison.OrdinalIgnoreCase);
                }
            }
            return true;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is FileLoaderNameCondition)) return 1;
            if (obj == this) return 0;
            return this.Position.CompareTo(((FileLoaderNameCondition)obj).Position);
        }

    }


    public static class FileLoaderNameConditionsExtensionMethods
    {
        public static ObservableCollection<string> GetAll(this FileNameCondition filenamecondition, Func<string, string> Localize)
        {
            ObservableCollection<string> conditions = new ObservableCollection<string>();
            conditions.Add(Localize?.Invoke("BEGINS_WITH"));
            conditions.Add(Localize?.Invoke("CONTAINS"));
            conditions.Add(Localize?.Invoke("DO_NOT_CONTAINS"));
            conditions.Add(Localize?.Invoke("ENDS_WITH"));
            return conditions;
        }
        public static string GetText(this FileNameCondition filenamecondition, Func<string, string> Localize)
        {
            if (FileNameCondition.BEGINS_WITH.Equals(filenamecondition))
            {
                return Localize?.Invoke("BEGINS_WITH");
            }
            if (FileNameCondition.CONTAINS.Equals(filenamecondition))
            {
                return Localize?.Invoke("CONTAINS");
            }
            if (FileNameCondition.DO_NOT_CONTAINS.Equals(filenamecondition))
            {
                return Localize?.Invoke("DO_NOT_CONTAINS");
            }
            if (FileNameCondition.ENDS_WITH.Equals(filenamecondition))
            {
                return Localize?.Invoke("ENDS_WITH");
            }

            return null;
        }

        public static FileNameCondition GetFilenameCondition(this FileNameCondition filenamecondition, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("BEGINS_WITH")))
                {
                    return FileNameCondition.BEGINS_WITH;
                }
                if (text.Equals(Localize?.Invoke("CONTAINS")))
                {
                    return FileNameCondition.CONTAINS;
                }
                if (text.Equals(Localize?.Invoke("DO_NOT_CONTAINS")))
                {
                    return FileNameCondition.DO_NOT_CONTAINS;
                }
                if (text.Equals(Localize?.Invoke("ENDS_WITH")))
                {
                    return FileNameCondition.ENDS_WITH;
                }

            }
            return FileNameCondition.BEGINS_WITH;
        }

    }

}
