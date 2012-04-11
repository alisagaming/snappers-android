package net.hockeyapp.android;

public interface DownloadFileListener {
  public void downloadFailed(DownloadFileTask task, Boolean userWantsRetry);
  public void downloadSuccessful(DownloadFileTask task);
}
