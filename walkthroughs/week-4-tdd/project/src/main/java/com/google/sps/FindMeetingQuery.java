// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License. 

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public final class FindMeetingQuery {

  private Collection<String> mAttendees;
  private long mDuration;
  private final List<TimeRange> mTimeRanges = new ArrayList<>();
  private final Collection<TimeRange> mSlots = new ArrayList<>();

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    mAttendees = request.getAttendees();
    mDuration = request.getDuration();
    mTimeRanges.clear();
    mSlots.clear();

    // Get all TimeRange of events containing request attendees
    for (Event e: events){
      for (String a: mAttendees){
        if (e.getAttendees().contains(a)){
          TimeRange cur = e.getWhen();
          mTimeRanges.add(cur);
        }
      }
    }
    // Sort with ascending order
    Collections.sort(mTimeRanges, TimeRange.ORDER_BY_START);

    if (TimeRange.START_OF_DAY + mDuration > TimeRange.END_OF_DAY+1) return mSlots;

    if (mTimeRanges.isEmpty()) {
      mSlots.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    } else {
      // Find slots before first busy event
      if (TimeRange.START_OF_DAY + mDuration <= mTimeRanges.get(0).start()){
        mSlots.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, mTimeRanges.get(0).start(), false));
      }
      // Find slots between busy events
      final int N = mTimeRanges.size();
      TimeRange prev;
      TimeRange next;
      int latestEndTime = mTimeRanges.get(0).end();
      for (int i = 0, j = i+1; i < N-1; i++, j++){
        prev = mTimeRanges.get(i);
        next = mTimeRanges.get(j);
        int overlapEndTime = prev.end();
        while(j < N-1 && prev.overlaps(next)){
          overlapEndTime = Math.max(overlapEndTime, next.end());
          j++;
          i++;
          prev = mTimeRanges.get(i);
          next = mTimeRanges.get(j);
        }
        latestEndTime = Math.max(latestEndTime, overlapEndTime);
        if (next.start() - overlapEndTime >= mDuration){
          mSlots.add(TimeRange.fromStartEnd(overlapEndTime, next.start(), false));
        }
      }
      latestEndTime = Math.max(latestEndTime, mTimeRanges.get(N-1).end());

      // Find slots after last busy event
      if (mTimeRanges.get(N-1).end() + mDuration <= TimeRange.END_OF_DAY+1){
        mSlots.add(TimeRange.fromStartEnd(latestEndTime, TimeRange.END_OF_DAY, true));
      }
    }
    return mSlots;
  }
}
