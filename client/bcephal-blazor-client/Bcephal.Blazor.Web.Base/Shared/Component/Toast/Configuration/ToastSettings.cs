﻿using System;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Toast
{
    public class ToastSettings
    {
        public ToastSettings(
            string heading,
            RenderFragment message,
            IconType? iconType,
            string baseClass,
            string additionalClasses,
            string icon,
            bool showProgressBar,
            Action? onClick)
        {
            Heading = heading;
            Message = message;
            IconType = iconType;
            BaseClass = baseClass;
            AdditionalClasses = additionalClasses;
            Icon = icon;
            ShowProgressBar = showProgressBar;
            OnClick = onClick;
            if (onClick != null)
            {
                AdditionalClasses += " blazored-toast-action";
            }
        }

        public string Heading { get; set; }
        public RenderFragment Message { get; set; }
        public string BaseClass { get; set; }
        public string AdditionalClasses { get; set; }
        public string Icon { get; set; }
        public IconType? IconType { get; set; }
        public bool ShowProgressBar { get; set; }
        public Action? OnClick { get; set; }
    }
}
