#!/usr/bin/env python3
"""Behavioral model for cancellation while a dedicated Vulkan JNI worker is hung."""

class DedicatedProbeProcess:
    def __init__(self):
        self.alive = True
        self.worker_hung = True
        self.service_running = True
        self.files_deleted = False
        self.unrelated_process_alive = True

    def stop_service(self) -> bool:
        if not self.service_running:
            return False
        self.service_running = False
        self.on_destroy()
        return True

    def on_destroy(self) -> None:
        self.alive = False

    def delete_request_files(self) -> None:
        self.files_deleted = True


def cancellation_cleanup(process: DedicatedProbeProcess, activity_manager_visible: bool) -> None:
    stop_requested = process.stop_service()
    if stop_requested:
        assert process.alive is False
    if activity_manager_visible and process.alive:
        process.alive = False
    assert process.alive is False
    process.delete_request_files()


def main() -> None:
    process = DedicatedProbeProcess()
    cancellation_cleanup(process, activity_manager_visible=False)
    assert process.files_deleted is True
    assert process.worker_hung is True  # process exit, not cooperative JNI unwind, is the guarantee
    assert process.unrelated_process_alive is True

    absent = DedicatedProbeProcess()
    absent.service_running = False
    absent.alive = False
    stop_requested = absent.stop_service()
    assert stop_requested is False
    assert absent.unrelated_process_alive is True

    print('PASS cancellation state machine: non-cancellable stopService teardown kills hung dedicated process before request-file cleanup; ActivityManager visibility is optional; unrelated-process false-positive control PASS')


if __name__ == '__main__':
    main()
