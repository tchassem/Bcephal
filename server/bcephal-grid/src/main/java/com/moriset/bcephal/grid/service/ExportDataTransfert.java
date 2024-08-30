package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.grid.domain.GrilleExportDataType;

public class ExportDataTransfert {
	public enum Decision {
		END, STOP, CONTINUE, NEW;

		public boolean isEnd() {
			return this == END;
		}

		public boolean isStop() {
			return this == STOP;
		}

		public boolean isContinue() {
			return this == CONTINUE;
		}

		public boolean isNew() {
			return this == NEW;
		}
	}

	public byte[] data;
	public Decision decision;
	public GrilleExportDataType type;

	public ExportDataTransfert() {
		decision = Decision.CONTINUE;
	}

	public ExportDataTransfert(byte[] data, GrilleExportDataType type) {
		this();
		this.data = data;
		this.type = type;
	}

	public ExportDataTransfert(byte[] data, Decision decision, GrilleExportDataType type) {
		this(data, type);
		this.decision = decision;
	}

	public ExportDataTransfert(byte[] data, String decision, GrilleExportDataType type) {
		this(data, type);
		this.decision = Decision.valueOf(decision);
	}
}
