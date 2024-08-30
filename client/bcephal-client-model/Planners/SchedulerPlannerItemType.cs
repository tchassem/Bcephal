using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerItemType
    {
        public static SchedulerPlannerItemType ACTION = new SchedulerPlannerItemType("ACTION", "ACTION");
        public static SchedulerPlannerItemType BILLING = new SchedulerPlannerItemType("BILLING", "BILLING");
        public static SchedulerPlannerItemType CHECK = new SchedulerPlannerItemType("CHECK", "CHECK");
        public static SchedulerPlannerItemType JOIN = new SchedulerPlannerItemType("JOIN", "JOIN");
        public static SchedulerPlannerItemType RECO = new SchedulerPlannerItemType("RECO", "RECO");
        public static SchedulerPlannerItemType REFRESH_PUBLICATIONS = new SchedulerPlannerItemType("REFRESH_PUBLICATIONS", "REFRESH_PUBLICATIONS");
        public static SchedulerPlannerItemType ROUTINE = new SchedulerPlannerItemType("ROUTINE", "ROUTINE");
        public static SchedulerPlannerItemType TEMPORISATION = new SchedulerPlannerItemType("TEMPORISATION", "TEMPORISATION");
        public static SchedulerPlannerItemType TRANSFORMATION_TREE = new SchedulerPlannerItemType("TRANSFORMATION_TREE", "TRANSFORMATION_TREE");


        public string label;
        public string code;

        public SchedulerPlannerItemType(string code, string label)
        {
            this.code = code;
            this.label = label;
        }

        public string GetLabel()
        {
            return label;
        }

        public String getCode()
        {
            return code;
        }

        public bool IsAction()
        {
            return this == ACTION;
        }

        public bool IsBilling()
        {
            return this == BILLING;
        }

        public bool IsCheck()
        {
            return this == CHECK;
        }

        public bool IsJoin()
        {
            return this == JOIN;
        }

        public bool IsReco()
        {
            return this == RECO;
        }

        public bool IsRefreshPublication()
        {
            return this == REFRESH_PUBLICATIONS;
        }

        public bool IsRoutine()
        {
            return this == ROUTINE;
        }

        public bool IsTemporisation()
        {
            return this == TEMPORISATION;
        }

        public bool IsTransformationTree()
        {
            return this == TRANSFORMATION_TREE;
        }

        public override string ToString()
        {
            return GetLabel();
        }

        public static SchedulerPlannerItemType GetByLabel(string label)
        {
            if (label == null) return null;
            if (ACTION.label.Equals(label)) return ACTION;
            if (BILLING.label.Equals(label)) return BILLING;
            if (CHECK.label.Equals(label)) return CHECK;
            if (JOIN.label.Equals(label)) return JOIN;
            if (REFRESH_PUBLICATIONS.label.Equals(label)) return REFRESH_PUBLICATIONS;
            if (RECO.label.Equals(label)) return RECO;
            if (ROUTINE.label.Equals(label)) return ROUTINE;
            if (TEMPORISATION.label.Equals(label)) return TEMPORISATION;
            if (TRANSFORMATION_TREE.label.Equals(label)) return TRANSFORMATION_TREE;
            return null;
        }

        public static SchedulerPlannerItemType GetByCode(string code)
        {
            if (code == null) return null;
            if (ACTION.code.Equals(code)) return ACTION;
            if (BILLING.code.Equals(code)) return BILLING;
            if (CHECK.code.Equals(code)) return CHECK;
            if (JOIN.code.Equals(code)) return JOIN;
            if (RECO.code.Equals(code)) return RECO;
            if (REFRESH_PUBLICATIONS.code.Equals(code)) return REFRESH_PUBLICATIONS;
            if (ROUTINE.code.Equals(code)) return ROUTINE;
            if (TEMPORISATION.code.Equals(code)) return TEMPORISATION;
            if (TRANSFORMATION_TREE.code.Equals(code)) return TRANSFORMATION_TREE;
            return null;
        }
    }

    public static class SchedulerPlannerItemTypeExtensionMethods
    {      

        public static ObservableCollection<string> GetAll(this SchedulerPlannerItemType itemType, Func<string, string> Localize)
        {
            ObservableCollection<string> itemTypes = new ObservableCollection<string>();
            itemTypes.Add(null);
            itemTypes.Add(Localize?.Invoke("ACTION"));
            itemTypes.Add(Localize?.Invoke("billing"));
            itemTypes.Add(Localize?.Invoke("CHECK"));
            itemTypes.Add(Localize?.Invoke("JOIN"));
            itemTypes.Add(Localize?.Invoke("RECO"));
            itemTypes.Add(Localize?.Invoke("REFRESH_PUBLICATIONS"));
            itemTypes.Add(Localize?.Invoke("ROUTINE"));
            itemTypes.Add(Localize?.Invoke("TEMPORISATION"));
            itemTypes.Add(Localize?.Invoke("TRANSFORMATION_TREE"));
            return itemTypes;
        }

        public static string GetText(this SchedulerPlannerItemType itemType, Func<string, string> Localize)
        {
            if (SchedulerPlannerItemType.ACTION.Equals(itemType))
            {
                return Localize?.Invoke("ACTION");
            }
            if (SchedulerPlannerItemType.BILLING.Equals(itemType))
            {
                return Localize?.Invoke("billing");
            }
            if (SchedulerPlannerItemType.CHECK.Equals(itemType))
            {
                return Localize?.Invoke("CHECK");
            }
            if (SchedulerPlannerItemType.JOIN.Equals(itemType))
            {
                return Localize?.Invoke("JOIN");
            }
            if (SchedulerPlannerItemType.RECO.Equals(itemType))
            {
                return Localize?.Invoke("RECO");
            }
            if (SchedulerPlannerItemType.REFRESH_PUBLICATIONS.Equals(itemType))
            {
                return Localize?.Invoke("REFRESH_PUBLICATIONS");
            }
            if (SchedulerPlannerItemType.ROUTINE.Equals(itemType))
            {
                return Localize?.Invoke("ROUTINE");
            }
            if (SchedulerPlannerItemType.TEMPORISATION.Equals(itemType))
            {
                return Localize?.Invoke("TEMPORISATION");
            }
            if (SchedulerPlannerItemType.TRANSFORMATION_TREE.Equals(itemType))
            {
                return Localize?.Invoke("TRANSFORMATION_TREE");
            }
            return null;
        }

        public static SchedulerPlannerItemType GetItemType(this SchedulerPlannerItemType itemType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("ACTION")))
                {
                    return SchedulerPlannerItemType.ACTION;
                }
                if (text.Equals(Localize?.Invoke("billing")))
                {
                    return SchedulerPlannerItemType.BILLING;
                }
                if (text.Equals(Localize?.Invoke("CHECK")))
                {
                    return SchedulerPlannerItemType.CHECK;
                }
                if (text.Equals(Localize?.Invoke("JOIN")))
                {
                    return SchedulerPlannerItemType.JOIN;
                }
                if (text.Equals(Localize?.Invoke("RECO")))
                {
                    return SchedulerPlannerItemType.RECO;
                }
                if (text.Equals(Localize?.Invoke("REFRESH_PUBLICATIONS")))
                {
                    return SchedulerPlannerItemType.REFRESH_PUBLICATIONS;
                }
                if (text.Equals(Localize?.Invoke("ROUTINE")))
                {
                    return SchedulerPlannerItemType.ROUTINE;
                }
                if (text.Equals(Localize?.Invoke("TEMPORISATION")))
                {
                    return SchedulerPlannerItemType.TEMPORISATION;
                }
                if (text.Equals(Localize?.Invoke("TRANSFORMATION_TREE")))
                {
                    return SchedulerPlannerItemType.TRANSFORMATION_TREE;
                }
            }
            return SchedulerPlannerItemType.ACTION;
        }

        public static SchedulerPlannerItemType Parse(this SchedulerPlannerItemType itemType, string text)
        {
            try
            {
                return string.IsNullOrWhiteSpace(text) ? null : ((SchedulerPlannerItemType)Enum.Parse(typeof(SchedulerPlannerItemType), text));
            }
            catch
            {
                return null;
            }
        }
    }
}