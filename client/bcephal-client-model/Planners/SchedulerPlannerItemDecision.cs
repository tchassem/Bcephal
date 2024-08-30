using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerItemDecision
    {

        public static SchedulerPlannerItemDecision CONTINUE = new SchedulerPlannerItemDecision("CONTINUE", "Continue");
        public static SchedulerPlannerItemDecision GOTO = new SchedulerPlannerItemDecision("GOTO", "Go to");
        public static SchedulerPlannerItemDecision MESSAGE_WITH_CONFIRM = new SchedulerPlannerItemDecision("MESSAGE_WITH_CONFIRM", "Send message with confirmation");
        public static SchedulerPlannerItemDecision MESSAGE_WITHOUT_CONFIRM = new SchedulerPlannerItemDecision("MESSAGE_WITHOUT_CONFIRM", "Send message");
        public static SchedulerPlannerItemDecision RESTART = new SchedulerPlannerItemDecision("RESTART", "Restart");
        public static SchedulerPlannerItemDecision SKIP_NEXT = new SchedulerPlannerItemDecision("SKIP_NEXT", "Skip next setp");
        public static SchedulerPlannerItemDecision STOP = new SchedulerPlannerItemDecision("STOP", "Stop");

        public string label;
        public string code;

        public SchedulerPlannerItemDecision(string code, string label)
        {
            this.code = code;
            this.label = label;
        }

        public string GetLabel()
        {
            return label;
        }

        public bool IsGoto()
        {
            return this == GOTO;
        }

        public bool IsContinue()
        {
            return this == CONTINUE;
        }

        public bool IsMessageWithConfirmation()
        {
            return this == MESSAGE_WITH_CONFIRM;
        }

        public bool IsMessageWithoutConfirmation()
        {
            return this == MESSAGE_WITHOUT_CONFIRM;
        }

        public bool IsRestart()
        {
            return this == RESTART;
        }

        public bool IsSkipNext()
        {
            return this == SKIP_NEXT;
        }

        public bool IsStop()
        {
            return this == STOP;
        }

        public override string ToString()
        {
            return GetLabel();
        }

        public static SchedulerPlannerItemDecision GetByLabel(string label)
        {
            if (label == null) return null;
            if (CONTINUE.label.Equals(label)) return CONTINUE;
            if (GOTO.label.Equals(label)) return GOTO;
            if (MESSAGE_WITH_CONFIRM.label.Equals(label)) return MESSAGE_WITH_CONFIRM;
            if (MESSAGE_WITHOUT_CONFIRM.label.Equals(label)) return MESSAGE_WITHOUT_CONFIRM;
            if (RESTART.label.Equals(label)) return RESTART;
            if (SKIP_NEXT.label.Equals(label)) return SKIP_NEXT;
            if (STOP.label.Equals(label)) return STOP;
            return null;
        }

        public static SchedulerPlannerItemDecision GetByCode(string code)
        {
            if (code == null) return null;
            if (CONTINUE.code.Equals(code)) return CONTINUE;
            if (GOTO.code.Equals(code)) return GOTO;
            if (MESSAGE_WITH_CONFIRM.code.Equals(code)) return MESSAGE_WITH_CONFIRM;
            if (MESSAGE_WITHOUT_CONFIRM.code.Equals(code)) return MESSAGE_WITHOUT_CONFIRM;
            if (RESTART.code.Equals(code)) return RESTART;
            if (SKIP_NEXT.code.Equals(code)) return SKIP_NEXT;
            if (STOP.code.Equals(code)) return STOP;
            return null;
        }
    }



    public static class SchedulerPlannerItemActionExtention
    {
        public static ObservableCollection<string> GetAll(this SchedulerPlannerItemDecision schedulerPlannerItemDecision, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("CONTINUE"));
            operators.Add(Localize?.Invoke("GOTO"));
            operators.Add(Localize?.Invoke("MESSAGE_WITH_CONFIRM"));
            operators.Add(Localize?.Invoke("MESSAGE_WITHOUT_CONFIRM"));
            operators.Add(Localize?.Invoke("RESTART_"));
            operators.Add(Localize?.Invoke("SKIP_NEXT"));
            operators.Add(Localize?.Invoke("STOP_"));

            return operators;
        }

        public static string GetText(this SchedulerPlannerItemDecision schedulerPlannerItemDecision, Func<string, string> Localize)
        {
            if (schedulerPlannerItemDecision.IsContinue())
            {
                return Localize?.Invoke("CONTINUE");
            }
            if (schedulerPlannerItemDecision.IsGoto())
            {
                return Localize?.Invoke("GOTO");
            }
            if (schedulerPlannerItemDecision.IsMessageWithConfirmation())
            {
                return Localize?.Invoke("MESSAGE_WITH_CONFIRM");
            }
            if (schedulerPlannerItemDecision.IsMessageWithoutConfirmation())
            {
                return Localize?.Invoke("MESSAGE_WITHOUT_CONFIRM");
            }
            if (schedulerPlannerItemDecision.IsRestart())
            {
                return Localize?.Invoke("RESTART_");
            }
            if (schedulerPlannerItemDecision.IsSkipNext())
            {
                return Localize?.Invoke("SKIP_NEXT");
            }
             if (schedulerPlannerItemDecision.IsStop())
            {
                return Localize?.Invoke("STOP_");
            }
            

            return null;
        }

        public static SchedulerPlannerItemDecision GetSchedulerPlannerItemDecision(this SchedulerPlannerItemDecision schedulerPlannerItemDecision, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("CONTINUE")))
                {
                    return SchedulerPlannerItemDecision.CONTINUE;
                }
                if (text.Equals(Localize?.Invoke("GOTO")))
                {
                    return SchedulerPlannerItemDecision.GOTO;
                }
                if (text.Equals(Localize?.Invoke("MESSAGE_WITH_CONFIRM")))
                {
                    return SchedulerPlannerItemDecision.MESSAGE_WITH_CONFIRM;
                }
                if (text.Equals(Localize?.Invoke("MESSAGE_WITHOUT_CONFIRM")))
                {
                    return SchedulerPlannerItemDecision.MESSAGE_WITHOUT_CONFIRM;
                }
                if (text.Equals(Localize?.Invoke("RESTART_")))
                {
                    return SchedulerPlannerItemDecision.RESTART;
                }
                 if (text.Equals(Localize?.Invoke("SKIP_NEXT")))
                {
                    return SchedulerPlannerItemDecision.SKIP_NEXT;
                }
                  if (text.Equals(Localize?.Invoke("STOP_")))
                {
                    return SchedulerPlannerItemDecision.STOP;
                }

            }
            return null;
        }

        public static SchedulerPlannerItemDecision Parse(this SchedulerPlannerItemDecision itemType, string text)
        {
            try
            {
                return string.IsNullOrWhiteSpace(text) ? null : ((SchedulerPlannerItemDecision)Enum.Parse(typeof(SchedulerPlannerItemDecision), text));
            }
            catch
            {
                return null;
            }
        }
    }
}