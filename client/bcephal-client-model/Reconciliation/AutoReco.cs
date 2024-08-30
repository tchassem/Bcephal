using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Routines;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoReco : RecoConditianable
    {

		public UniverseFilter LeftFilter { get; set; }
        public UniverseFilter RightFilter { get; set; }

        public long? RecoId { get; set; }

		public string Method { get; set; }
		[JsonIgnore]
		public AutoRecoMethod AutoRecoMethod
		{
			get
			{
				return string.IsNullOrEmpty(Method) ? AutoRecoMethod.ONE_ON_ONE : AutoRecoMethod.GetByCode(Method);
			}
			set
			{
				this.Method = value != null ? value.getCode() : null;
			}
		}

		public bool UseCombinations { get; set; }

		public int MaxDurationPerLine { get; set; }

		public string Condition { get; set; }
		[JsonIgnore]
		public AutoRecoCondition AutoRecoCondition
		{
			get
			{
				return string.IsNullOrEmpty(Condition) ? AutoRecoCondition.BALANCE_IS_ZERO : AutoRecoCondition.GetByCode(Condition);
			}
			set
			{
				this.Condition = value != null ? value.getCode() : null;
			}
		}

		public decimal? ConditionMinValue { get; set; }

		public decimal? ConditionMaxValue { get; set; }

        public string Script { get; set; }

		public ListChangeHandler<RoutineExecutor> RoutineListChangeHandler { get; set; }


		public AutoReco() :base()
		{
			this.RoutineListChangeHandler = new ListChangeHandler<RoutineExecutor>();
		}


        public void AddRoutine(RoutineExecutor routine, bool sort = true)
        {
            routine.Position = RoutineListChangeHandler.Items.Count;
            RoutineListChangeHandler.AddNew(routine, sort);
        }

        public void UpdateRoutine(RoutineExecutor routine, bool sort = true)
        {
            RoutineListChangeHandler.AddUpdated(routine, sort);
        }

        public void DeleteOrForgetRoutine(RoutineExecutor routine)
        {
            if (routine.IsPersistent)
            {
                DeleteRoutine(routine);
            }
            else
            {
                ForgetRoutine(routine);
            }
        }

        public void DeleteRoutine(RoutineExecutor routine)
        {
            RoutineListChangeHandler.AddDeleted(routine);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetRoutine(RoutineExecutor routine)
        {
            RoutineListChangeHandler.forget(routine);
            foreach (RoutineExecutor child in RoutineListChangeHandler.Items)
            {
                if (child.Position > routine.Position)
                {
                    child.Position = child.Position - 1;
                    RoutineListChangeHandler.AddUpdated(child, false);
                }
            }
        }

    }
}
