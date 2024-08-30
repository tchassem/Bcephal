using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class DataForEdit: INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        
        [JsonIgnore]
        private bool _IsInEditMode  = false;
        [JsonIgnore]
        public bool _IsInNewEditMode { get; set; } = false;
        [JsonIgnore]
        public bool IsInEditMode
        {
            get => _IsInEditMode;
            set /*=> SetPropertyValue(ref _IsInEditMode, value);*/
            {
                if (value != this._IsInEditMode)
                {
                    this._IsInEditMode = value;
                    NotifyPropertyChanged();
                }
            }
        }
        private void NotifyPropertyChanged([CallerMemberName] String propertyName = "")
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        public event PropertyChangedEventHandler PropertyStateChanged;
        [JsonIgnore]
        private bool _CanEditRefreshGridStatus = false;
        [JsonIgnore]
        public bool CanEditRefreshGridStatus
        {
            get => _CanEditRefreshGridStatus;
            set => SetPropertyValue(ref _CanEditRefreshGridStatus, value);            
        }
        void SetPropertyValue<T>(ref T property, T value, [CallerMemberName] string propertyName = "")
        {
            if (EqualityComparer<T>.Default.Equals(property, value))
                return;
            property = value;
            PropertyStateChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
