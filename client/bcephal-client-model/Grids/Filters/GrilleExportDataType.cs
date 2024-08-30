using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids.Filters
{
    public class GrilleExportDataType
    {

        public static GrilleExportDataType EXCEL = new GrilleExportDataType("EXCEL", "EXCEL", ".xlsx");
        public static GrilleExportDataType CSV = new GrilleExportDataType("CSV", "CSV", ".csv");
        public static GrilleExportDataType JSON = new GrilleExportDataType("JSON", "JSON", ".json");

        public string label;
        public string code;
        public string extension;
        private GrilleExportDataType(String code, String label, string extension)
        {
            this.label = label;
            this.code = code;
            this.extension = extension;
        }

        public static GrilleExportDataType GetByCode(String code)
        {
            if (code == null) return EXCEL;
            if (CSV.code.Equals(code)) return CSV;
            if (EXCEL.code.Equals(code)) return EXCEL;
            if (JSON.code.Equals(code)) return JSON;
            return EXCEL;
        }

        public static ObservableCollection<GrilleExportDataType> GetAll()
        {
            ObservableCollection<GrilleExportDataType> operators = new ObservableCollection<GrilleExportDataType>();
            operators.Add(GrilleExportDataType.CSV);
            operators.Add(GrilleExportDataType.JSON);
            operators.Add(GrilleExportDataType.EXCEL);
            return operators;
        }

        public override string ToString()
        {
            return this.label;
        }

    }

    public class DataType
    {

        public static DataType EXCEL = new DataType("EXCEL", "EXCEL", ".xlsx");
        public static DataType CSV = new DataType("CSV", "CSV", ".csv");
        public static DataType JSON = new DataType("JSON", "JSON", ".json");

        public string label;
        public string code;
        public string extension;
        private DataType(String code, String label, string extension)
        {
            this.label = label;
            this.code = code;
            this.extension = extension;
        }

        public static DataType GetByCode(String code)
        {
            if (code == null) return EXCEL;
            if (CSV.code.Equals(code)) return CSV;
            if (EXCEL.code.Equals(code)) return EXCEL;
            if (JSON.code.Equals(code)) return JSON;
            return EXCEL;
        }

        public static ObservableCollection<DataType> GetAll()
        {
            ObservableCollection<DataType> operators = new ObservableCollection<DataType>();
            operators.Add(DataType.CSV);
            operators.Add(DataType.JSON);
            operators.Add(DataType.EXCEL);
            return operators;
        }

        public override string ToString()
        {
            return this.label;
        }

    }
}
