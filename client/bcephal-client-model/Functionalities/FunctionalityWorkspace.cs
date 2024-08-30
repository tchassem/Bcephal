using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Functionalities
{
    public class FunctionalityWorkspace
    {
        public ObservableCollection<Functionality> AvailableFunctionalities { get; set; }
        public ObservableCollection<FunctionalityBlockGroup> FunctionalityBlockGroups { get; set; }

        public Functionality GetFunctionality(string code)
        {
            foreach (Functionality functionality in AvailableFunctionalities)
            {
                if (functionality.Code.Equals(code))
                {
                    return functionality;
                }
            }
            return null;
        }
    }
}
