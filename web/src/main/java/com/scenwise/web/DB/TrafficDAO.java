package com.scenwise.web.DB;

import java.util.Collection;

public interface TrafficDAO<T> {
  void batchSave(Collection<T> measurements);
  void createTableIfNotExists();
}

