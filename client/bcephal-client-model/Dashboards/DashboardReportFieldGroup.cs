﻿using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
	[JsonConverter(typeof(StringEnumConverter))]
	public  enum DashboardReportFieldGroup
    {
		ROW,
		COLUMN,
		COMMON,
		FILTER,
		GROUP,
	}
}
