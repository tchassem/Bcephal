using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class JobExecutedEventArgs : EventArgs { }

    public class PeriodicExecutor : IDisposable
    {
        public event EventHandler<JobExecutedEventArgs> JobExecuted;
        void OnJobExecuted()
        {
            JobExecuted?.Invoke(this, new JobExecutedEventArgs());
        }

        Timer _Timer;
        bool _Running;
        string starterclass;

        public void StartExecuting(string _starterclass)
        {
            this.starterclass = _starterclass;
            if (!_Running)
            {
                // Initiate a Timer
                _Timer = new Timer();
                _Timer.Interval = 16000;  // every 16 sec
                _Timer.Elapsed += HandleTimer;
                _Timer.AutoReset = true;
                _Timer.Enabled = true;

                _Running = true;
            }
        }
        void HandleTimer(object source, ElapsedEventArgs e)
        {
            // Execute required job
            Console.WriteLine($"Je m'exécute à partir de {starterclass} !!");
            // Notify any subscribers to the event
            OnJobExecuted();
        }

        public void Dispose()
        {
            if (_Running)
            {
                // Clear up the timer
                _Timer.Dispose();
            }
        }
    }
}
