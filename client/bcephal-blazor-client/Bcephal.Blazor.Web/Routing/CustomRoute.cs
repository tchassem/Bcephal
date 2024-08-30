using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

namespace Bcephal.Blazor.Web.Routing
{
    public class CustomRoute
    {
        public string[] UriSegments { get; set; }
        public Type Handler { get; set; }

        public Dictionary<string, object> UriParameters { get; } = new Dictionary<string, object>();

    public MatchResult Match(string[] segments)
        {
            if (segments.Length != UriSegments.Length)
            {
                return MatchResult.NoMatch();
            }
             for (var i = 0; i < UriSegments.Length; i++)
            {
                if (UriSegments[i].Contains("["))
                {
                    var expv = getParttern(UriSegments[i]);
                    Type expType = expv.Keys.FirstOrDefault();
                    expv.TryGetValue(expType, out string valueExp);
                    var macth = Regex.Match(segments[i], valueExp, RegexOptions.IgnoreCase);
                    if (!macth.Success)
                    {
                        return MatchResult.NoMatch();
                    }
                    string key = getKey(UriSegments[i]);
                    if (UriParameters.ContainsKey(key))
                    {
                        UriParameters.Remove(key);
                    }
                    converter(key, segments[i], macth, expType);
                }
                else
                if (string.Compare(segments[i], UriSegments[i], StringComparison.OrdinalIgnoreCase) != 0)
                {
                    return MatchResult.NoMatch();
                }
            }
            //Console.WriteLine("UriSegments route :Success ");
            return MatchResult.Match(this);
        }

        private string getKey(string value)
        {
            if (string.IsNullOrWhiteSpace(value))
            {
                return null;
            }
            string v = value;
            string el = "[";
            if (v.Contains(el))
            {
                v = v.Substring(1);
            }
            el = ":>>>>[";
            if (v.Contains(el))
            {
                v = v.Substring(0, v.IndexOf(el));
            }
            return v;
        }

        private Dictionary<Type, string> getParttern(string value)
        {
            if (string.IsNullOrWhiteSpace(value))
            {
                return null;
            }
            string v = value;
            string el = "[";
            if (v.Contains(el))
            {
                v = v.Substring(1);
            }
            el = ":>>>>[";
            if (v.Contains(el))
            {
                v = v.Substring(v.IndexOf(el) + el.Length);
            }
            var exp = new Dictionary<Type, string>();
            el = "do";
            if (v.Contains(el))
            {
                v = v.Substring(2);
                exp.Add(typeof(double), v);
                return exp;
            }
            el = "i";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(int), v);
                return exp;
            }
            el = "l";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(long), v);
                return exp;
            }
            el = "b";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(bool), v);
                return exp;
            }
            el = "g";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(Guid), v);
                return exp;
            }
            el = "f";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(float), v);
                return exp;
            }
            el = "a";
            if (v.Contains(el))
            {
                v = v.Substring(1);
                exp.Add(typeof(decimal), v);
                return exp;
            }
            exp.Add(typeof(string), v);
            return exp;
        }

        private void converter(string Key, object value_, Match Match, Type type)
        {
            //Console.WriteLine("UriSegments Param key : " + Key + " , value :  " + value_);
            string matchValue = string.Empty;
            int i = 0;
            if (Match.Groups.Count > 0)
            {
                matchValue = Match.Groups[i].Value;
            }
           // Console.WriteLine("UriSegments matchValue : " + matchValue);
            var ts = new TypeSwitch()
                    .Case(typeof(int), ( x) =>
                    {
                        //Console.WriteLine("cast int ");
                        if (int.TryParse(matchValue, out int value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(long), ( x) =>
                    {
                        //Console.WriteLine("cast long ");
                        if (long.TryParse(matchValue, out long value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(float), (x) =>
                    {
                        //Console.WriteLine("cast float ");
                        if (float.TryParse(matchValue, out float value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(double), (x) =>
                    {
                        //Console.WriteLine("cast double ");
                        if (double.TryParse(matchValue, out double value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(Guid), (x) =>
                    {
                        //Console.WriteLine("cast Guid ");
                        if (Guid.TryParse(matchValue, out Guid value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(decimal), (x) =>
                    {
                        //Console.WriteLine("cast decimal ");
                        if (decimal.TryParse(matchValue, out decimal value))
                            UriParameters.Add(Key, value);
                        else
                            UriParameters.Add(Key, value_);
                    })
                    .Case(typeof(string), (x) =>
                    {
                       // Console.WriteLine("cast string ");
                        UriParameters.Add(Key, value_);
                    });

                ts.Switch(type,value_);
           
        }

    }

    public class TypeSwitch
    {
        private Dictionary<Type, Action<object>> matches = new Dictionary<Type, Action<object>>();
        public void Switch(Type type, object x) { matches[type](x); }
        public TypeSwitch Case(Type type , Action<object> action)
        {
            matches.Add(type, (x) => action(x)); return this;
        }
    }
    
}
