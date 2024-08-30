using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Media;

namespace Bcephal.Models.Utils
{
    public class ColorToLongConverter
    {

        public enum ColorFormat
        {
            RGB, RGBA, ARGB
        }

        public static int ColorToDecimal(Color color, ColorFormat format = ColorFormat.ARGB)
        {
            switch (format)
            {
                default:
                case ColorFormat.RGB:
                    return color.R << 16 | color.G << 8 | color.B;
                case ColorFormat.RGBA:
                    return color.R << 24 | color.G << 16 | color.B << 8 | color.A;
                case ColorFormat.ARGB:
                    return color.A << 24 | color.R << 16 | color.G << 8 | color.B;
            }
        }

        public static Color DecimalToColor(int dec, ColorFormat format = ColorFormat.ARGB)
        {
            switch (format)
            {
                default:
                case ColorFormat.RGB:
                    return Color.FromRgb((byte)((dec >> 16) & 0xFF), (byte)((dec >> 8) & 0xFF), (byte)(dec & 0xFF));
                case ColorFormat.RGBA:
                    return Color.FromArgb((byte)(dec & 0xFF), (byte)((dec >> 24) & 0xFF), (byte)((dec >> 16) & 0xFF), (byte)((dec >> 8) & 0xFF));
                case ColorFormat.ARGB:
                    return Color.FromArgb((byte)((dec >> 24) & 0xFF), (byte)((dec >> 16) & 0xFF), (byte)((dec >> 8) & 0xFF), (byte)(dec & 0xFF));
            }
        }

    }
}
