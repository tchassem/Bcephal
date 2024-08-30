using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Filters
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum DimensionType
    {
        MEASURE,
        PERIOD,
        ATTRIBUTE,
        SPOT,
        LOOP,
        BILLING_EVENT,
        FREE
    }
    public static class DimensionTypeExtensionMethods
    {
        public static ObservableCollection<string> GetAll(this DimensionType dimensionType, Func<string, string> Localize)
        {
            ObservableCollection<string> dimensionTypes = new ObservableCollection<string>();
            dimensionTypes.Add(null);
            dimensionTypes.Add(Localize?.Invoke("MEASURE"));
            dimensionTypes.Add(Localize?.Invoke("PERIOD"));
            dimensionTypes.Add(Localize?.Invoke("ATTRIBUTE"));
            dimensionTypes.Add(Localize?.Invoke("SPOT"));
            dimensionTypes.Add(Localize?.Invoke("LOOP"));
            dimensionTypes.Add(Localize?.Invoke("billing.event"));
            dimensionTypes.Add(Localize?.Invoke("FREE"));
            return dimensionTypes;
        }

        public static ObservableCollection<DimensionType> GetAll()
        {
            ObservableCollection<DimensionType> dimensions = new ObservableCollection<DimensionType>();
            dimensions.Add(DimensionType.SPOT);
            dimensions.Add(DimensionType.MEASURE);
            dimensions.Add(DimensionType.PERIOD);
            dimensions.Add(DimensionType.ATTRIBUTE);
            dimensions.Add(DimensionType.LOOP);
            dimensions.Add(DimensionType.BILLING_EVENT);
            dimensions.Add(DimensionType.FREE);
            return dimensions;
        }

        public static bool IsMeasure(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.MEASURE;
        }

        public static bool IsPeriod(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.PERIOD;
        }

        public static bool IsAttribute(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.ATTRIBUTE;
        }

        public static bool IsSpot(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.SPOT;
        }

        public static bool IsLoop(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.LOOP;
        }

        public static bool IsBillingEvent(this DimensionType dimensionType)
        {
            return dimensionType == DimensionType.BILLING_EVENT;
        }

        public static ObservableCollection<string> GetAll(this DimensionType? dimensionType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("MEASURE"),
                Localize?.Invoke("PERIOD"),
                Localize?.Invoke("ATTRIBUTE"),
                Localize?.Invoke("SPOT"),
                Localize?.Invoke("LOOP"),
                Localize?.Invoke("billing.event"),
                Localize?.Invoke("FREE")
            };
            return operators;
        }

        public static string GetText(this DimensionType dimensionType, Func<string, string> Localize)
        {
            if (DimensionType.MEASURE.Equals(dimensionType))
            {
                return Localize?.Invoke("MEASURE");
            }
            if (DimensionType.PERIOD.Equals(dimensionType))
            {
                return Localize?.Invoke("PERIOD");
            }
            if (DimensionType.ATTRIBUTE.Equals(dimensionType))
            {
                return Localize?.Invoke("ATTRIBUTE");
            }
            if (DimensionType.SPOT.Equals(dimensionType))
            {
                return Localize?.Invoke("SPOT");
            }
            if (DimensionType.LOOP.Equals(dimensionType))
            {
                return Localize?.Invoke("LOOP");
            }
            if (DimensionType.BILLING_EVENT.Equals(dimensionType))
            {
                return Localize?.Invoke("billing.event");
            }
            if (DimensionType.FREE.Equals(dimensionType))
            {
                return Localize?.Invoke("FREE");
            }
            return null;
        }

        public static DimensionType GetDimensionType(this DimensionType joinCoditionItemType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("MEASURE")))
                {
                    return DimensionType.MEASURE;
                }
                if (text.Equals(Localize?.Invoke("PERIOD")))
                {
                    return DimensionType.PERIOD;
                }
                if (text.Equals(Localize?.Invoke("ATTRIBUTE")))
                {
                    return DimensionType.ATTRIBUTE;
                }
                if (text.Equals(Localize?.Invoke("SPOT")))
                {
                    return DimensionType.SPOT;
                }
                if (text.Equals(Localize?.Invoke("LOOP")))
                {
                    return DimensionType.LOOP;
                }
                if (text.Equals(Localize?.Invoke("billing.event")))
                {
                    return DimensionType.BILLING_EVENT;
                }
                if (text.Equals(Localize?.Invoke("FREE")))
                {
                    return DimensionType.FREE;
                }
            }
            return DimensionType.MEASURE;
        }
    }
}
