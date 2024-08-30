using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum AlarmAttachmentType
    {
        REPORT_GRID,
        REPORT_SPREADSHEET,
        GRAPH
    }
    public static class AlarmAttachmentTypeExtensionMethods
    {
        public static ObservableCollection<AlarmAttachmentType> GetAll()
        {
            ObservableCollection<AlarmAttachmentType> attachments = new ObservableCollection<AlarmAttachmentType>();
            attachments.Add(AlarmAttachmentType.REPORT_GRID);
            attachments.Add(AlarmAttachmentType.REPORT_SPREADSHEET);
            attachments.Add(AlarmAttachmentType.GRAPH);
            return attachments;
        }

        public static bool IsREPORT_GRID(this AlarmAttachmentType attachment)
        {
            return attachment == AlarmAttachmentType.REPORT_GRID;
        }

        public static bool IsREPORT_SPREADSHEET(this AlarmAttachmentType attachment)
        {
            return attachment == AlarmAttachmentType.REPORT_SPREADSHEET;
        }

        public static bool IsGRAPH(this AlarmAttachmentType attachment)
        {
            return attachment == AlarmAttachmentType.GRAPH;
        }

        public static ObservableCollection<string> GetAll(this AlarmAttachmentType? attachment, Func<string, string> Localize)
        {
            ObservableCollection<string> attachments = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("report.grid"),
                Localize?.Invoke("report.spreadsheet"),
                Localize?.Invoke("graph")
            };
            return attachments;
        }

        public static ObservableCollection<string> GetAllAttachments(this AlarmAttachmentType? attachment, Func<string, string> Localize)
        {
            ObservableCollection<string> attachments = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("report.grid"),
                Localize?.Invoke("report.spreadsheet"),
                Localize?.Invoke("graph")
            };
            return attachments;
        }

        public static string GetText(this AlarmAttachmentType attachment, Func<string, string> Localize)
        {
            if (AlarmAttachmentType.REPORT_GRID.Equals(attachment))
            {
                return Localize?.Invoke("report.grid");
            }
            if (AlarmAttachmentType.REPORT_SPREADSHEET.Equals(attachment))
            {
                return Localize?.Invoke("report.spreadsheet");
            }
            if (AlarmAttachmentType.GRAPH.Equals(attachment))
            {
                return Localize?.Invoke("graph");
            }
            return null;
        }

        public static AlarmAttachmentType GetAlarmAttachmentType(this AlarmAttachmentType attachment, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("report.grid")))
                {
                    return AlarmAttachmentType.REPORT_GRID;
                }
                if (text.Equals(Localize?.Invoke("report.spreadsheet")))
                {
                    return AlarmAttachmentType.REPORT_SPREADSHEET;
                }
                if (text.Equals(Localize?.Invoke("graph")))
                {
                    return AlarmAttachmentType.GRAPH;
                }
            }
            return AlarmAttachmentType.REPORT_GRID;
        }
    }
}
