package com.catalog.service.interfaces;

import com.catalog.model.Materie;

public interface IMaterieService extends
        IReadable<Materie>,
        IInsertable<Materie>,
        IUpdatable<Materie>,
        IDeletable<Materie> {
}
