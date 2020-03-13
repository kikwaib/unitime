/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
*/
package org.unitime.timetable.gwt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.unitime.timetable.gwt.command.client.GwtRpcRequest;
import org.unitime.timetable.gwt.command.client.GwtRpcResponse;
import org.unitime.timetable.gwt.shared.ClassAssignmentInterface.ClassAssignment;
import org.unitime.timetable.gwt.shared.ClassAssignmentInterface.ErrorMessage;
import org.unitime.timetable.gwt.shared.CourseRequestInterface.RequestedCourse;
import org.unitime.timetable.gwt.shared.OnlineSectioningInterface.EligibilityCheck;
import org.unitime.timetable.gwt.shared.OnlineSectioningInterface.GradeMode;
import org.unitime.timetable.gwt.shared.OnlineSectioningInterface.GradeModes;
import org.unitime.timetable.gwt.shared.OnlineSectioningInterface.EligibilityCheck.EligibilityFlag;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Tomas Muller
 */
public class SpecialRegistrationInterface implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static class SpecialRegistrationContext implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private boolean iSpecReg = false;
		private String iSpecRegRequestId = null;
		private boolean iSpecRegDisclaimerAccepted = false;
		private boolean iSpecRegTimeConfs = false;
		private boolean iSpecRegSpaceConfs = false;
		private boolean iSpecRegChangeRequestNote = false;
		private SpecialRegistrationStatus iSpecRegStatus = null;
		private String iNote;
		private String iDisclaimer;
		private boolean iCanRequire = true;
		private ChangeRequestorNoteInterface iChangeRequestorNote = null;

		public SpecialRegistrationContext() {}
		public SpecialRegistrationContext(SpecialRegistrationContext cx) {
			copy(cx);
		}
		public void copy(SpecialRegistrationContext cx) {
			iSpecReg = cx.iSpecReg;
			iSpecRegRequestId = cx.iSpecRegRequestId;
			iSpecRegDisclaimerAccepted = cx.iSpecRegDisclaimerAccepted;
			iSpecRegTimeConfs = cx.iSpecRegTimeConfs;
			iSpecRegSpaceConfs = cx.iSpecRegSpaceConfs;
			iSpecRegStatus = cx.iSpecRegStatus;
			iNote = cx.iNote;
			iCanRequire = cx.iCanRequire;
			iSpecRegChangeRequestNote = cx.iSpecRegChangeRequestNote;
		}
		
		public boolean isEnabled() { return iSpecReg; }
		public void setEnabled(boolean specReg) { iSpecReg = specReg; }
		public boolean hasRequestId() { return iSpecRegRequestId != null; }
		public String getRequestId() { return iSpecRegRequestId; }
		public void setRequestId(String id) { iSpecRegRequestId = id; }
		public boolean isCanSubmit() { return true; }
		public boolean isDisclaimerAccepted() { return iSpecRegDisclaimerAccepted; }
		public void setDisclaimerAccepted(boolean accepted) { iSpecRegDisclaimerAccepted = accepted; }
		public boolean areTimeConflictsAllowed() { return iSpecRegTimeConfs; }
		public void setTimeConflictsAllowed(boolean allow) { iSpecRegTimeConfs = allow; }
		public boolean areSpaceConflictsAllowed() { return iSpecRegSpaceConfs; }
		public void setSpaceConflictsAllowed(boolean allow) { iSpecRegSpaceConfs = allow; }
		public SpecialRegistrationStatus getStatus() { return iSpecRegStatus; }
		public void setStatus(SpecialRegistrationStatus status) { iSpecRegStatus = status; }
		public String getNote() { return iNote; }
		public void setNote(String note) { iNote = note; }
		public String getDisclaimer() { return iDisclaimer; }
		public void setDisclaimer(String disclaimer) { iDisclaimer = disclaimer; }
		public boolean hasDisclaimer() { return iDisclaimer != null && !iDisclaimer.isEmpty(); }
		public boolean isCanRequire() { return iCanRequire; }
		public boolean isAllowChangeRequestNote() { return iSpecRegChangeRequestNote; }
		public void setAllowChangeRequestNote(boolean changeRequestNote) { iSpecRegChangeRequestNote = changeRequestNote; } 
		public void update(EligibilityCheck check) {
			iSpecRegTimeConfs = check != null && check.hasFlag(EligibilityFlag.SR_TIME_CONF);
			iSpecRegSpaceConfs = check != null && check.hasFlag(EligibilityFlag.SR_LIMIT_CONF);
			iSpecReg = check != null && check.hasFlag(EligibilityFlag.CAN_SPECREG);
			iDisclaimer = (check != null ? check.getOverrideRequestDisclaimer() : null);
			iCanRequire = check == null || check.hasFlag(EligibilityFlag.CAN_REQUIRE);
			iSpecRegChangeRequestNote = check != null && check.hasFlag(EligibilityFlag.SR_CHANGE_NOTE);
		}
		public void reset() {
			iNote = null;
			iSpecReg = false;
			iSpecRegRequestId = null;
			iSpecRegDisclaimerAccepted = false;
			iSpecRegTimeConfs = false;
			iSpecRegSpaceConfs = false;
			iSpecRegStatus = null;
			iDisclaimer = null;
			iCanRequire = true;
			iSpecRegChangeRequestNote = false;
		}
		public void reset(EligibilityCheck check) {
			reset();
			if (check != null) update(check);
		}
		
		public void setChangeRequestorNote(ChangeRequestorNoteInterface changeRequestorNote) { iChangeRequestorNote = changeRequestorNote; }
		public ChangeRequestorNoteInterface getChangeRequestorNoteInterface() { return iChangeRequestorNote; }
	}
	
	public static class SpecialRegistrationEligibilityRequest implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		private String iRequestId;
		private Collection<ClassAssignmentInterface.ClassAssignment> iClassAssignments;
		private ArrayList<ErrorMessage> iErrors = null;
		
		public SpecialRegistrationEligibilityRequest() {}
		public SpecialRegistrationEligibilityRequest(Long sessionId, Long studentId, String requestId, Collection<ClassAssignmentInterface.ClassAssignment> assignments, Collection<ErrorMessage> errors) {
			iClassAssignments = assignments;
			iStudentId = studentId;
			iSessionId = sessionId;
			iRequestId = requestId;
			if (errors != null)
				iErrors = new ArrayList<ErrorMessage>(errors);
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
		public String getRequestId() { return iRequestId; }
		public boolean hasRequestId() { return iRequestId != null && !iRequestId.isEmpty(); }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		public Collection<ClassAssignmentInterface.ClassAssignment> getClassAssignments() { return iClassAssignments; }
		public void setClassAssignments(Collection<ClassAssignmentInterface.ClassAssignment> assignments) { iClassAssignments = assignments; }
		public void addError(ErrorMessage error) {
			if (iErrors == null) iErrors = new ArrayList<ErrorMessage>();
			iErrors.add(error);
		}
		public boolean hasErrors() {
			return iErrors != null && !iErrors.isEmpty();
		}
		public ArrayList<ErrorMessage> getErrors() { return iErrors; }
	}
	
	public static class SpecialRegistrationEligibilityResponse implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private String iMessage;
		private boolean iCanSubmit;
		private List<ErrorMessage> iErrors = null;
		private List<ErrorMessage> iDeniedErrors = null;
		private List<ErrorMessage> iCancelErrors = null;
		private Set<String> iCancelRequestIds = null;
		private Float iCredit = null;
		
		public SpecialRegistrationEligibilityResponse() {}
		public SpecialRegistrationEligibilityResponse(boolean canSubmit, String message) {
			iCanSubmit = canSubmit; iMessage = message;
		}
	
		public boolean isCanSubmit() { return iCanSubmit; }
		public void setCanSubmit(boolean canSubmit) { iCanSubmit = canSubmit; }
		
		public boolean hasMessage() { return iMessage != null && !iMessage.isEmpty(); }
		public String getMessage() { return iMessage; }
		public void setMessage(String message) { iMessage = message; }
		
		public void addError(ErrorMessage error) {
			if (iErrors == null) iErrors = new ArrayList<ErrorMessage>();
			iErrors.add(error);
		}
		public boolean hasErrors() {
			return iErrors != null && !iErrors.isEmpty();
		}
		public List<ErrorMessage> getErrors() { return iErrors; }
		public void setErrors(Collection<ErrorMessage> messages) {
			if (messages == null)
				iErrors = null;
			else
				iErrors = new ArrayList<ErrorMessage>(messages);
		}
		
		public void addCancelError(ErrorMessage error) {
			if (iCancelErrors == null) iCancelErrors = new ArrayList<ErrorMessage>();
			iCancelErrors.add(error);
		}
		public boolean hasCancelErrors() {
			return iCancelErrors != null && !iCancelErrors.isEmpty();
		}
		public List<ErrorMessage> getCancelErrors() { return iCancelErrors; }
		public void setCancelErrors(Collection<ErrorMessage> messages) {
			if (messages == null)
				iCancelErrors = null;
			else
				iCancelErrors = new ArrayList<ErrorMessage>(messages);
		}
		public void addCancelRequestId(String id) {
			if (iCancelRequestIds == null) iCancelRequestIds = new HashSet<String>();
			iCancelRequestIds.add(id);
		}
		public boolean hasCancelRequestIds() { return iCancelRequestIds != null && !iCancelRequestIds.isEmpty(); }
		public Set<String> getCancelRequestIds() { return iCancelRequestIds; }
		public boolean isToBeCancelled(String requestId) { return iCancelRequestIds != null && iCancelRequestIds.contains(requestId); }
		
		public void addDeniedError(ErrorMessage error) {
			if (iDeniedErrors == null) iDeniedErrors = new ArrayList<ErrorMessage>();
			iDeniedErrors.add(error);
		}
		public boolean hasDeniedErrors() {
			return iDeniedErrors != null && !iDeniedErrors.isEmpty();
		}
		public List<ErrorMessage> getDeniedErrors() { return iDeniedErrors; }
		public void setDeniedErrors(Collection<ErrorMessage> messages) {
			if (messages == null)
				iDeniedErrors = null;
			else
				iDeniedErrors = new ArrayList<ErrorMessage>(messages);
		}
		
		public void setCredit(Float credit) { iCredit = credit; }
		public boolean hasCredit() { return iCredit != null; }
		public Float getCredit() { return iCredit; }
	}
	
	public static enum SpecialRegistrationStatus implements IsSerializable, Serializable {
		Draft, Pending, Approved, Rejected, Cancelled,
		;
	}
	
	public static enum SpecialRegistrationOperation implements IsSerializable, Serializable {
		Add, Drop, Keep,
		;
	}
	
	public static class RetrieveSpecialRegistrationResponse implements IsSerializable, Serializable, Comparable<RetrieveSpecialRegistrationResponse> {
		private static final long serialVersionUID = 1L;
		private SpecialRegistrationStatus iStatus;
		private Date iSubmitDate;
		private String iRequestId;
		private String iDescription;
		private String iNote;
		private List<ClassAssignmentInterface.ClassAssignment> iChanges;
		private boolean iCanCancel = false;
		private boolean iHasTimeConflict, iHasSpaceConflict, iExtended;
		private ArrayList<ErrorMessage> iErrors = null;
		private Float iMaxCredit = null;
		
		public RetrieveSpecialRegistrationResponse() {}
		
		public Date getSubmitDate() { return iSubmitDate; }
		public void setSubmitDate(Date date) { iSubmitDate = date; }
		
		public String getRequestId() { return iRequestId; }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		
		public String getDescription() { return iDescription; }
		public void setDescription(String description) { iDescription = description; }
		
		public String getNote() { return iNote; }
		public void setNote(String note) { iNote = note; }
		
		public SpecialRegistrationStatus getStatus() { return iStatus; }
		public void setStatus(SpecialRegistrationStatus status) { iStatus = status; }
		
		public boolean hasChanges() { return iChanges != null && !iChanges.isEmpty(); }
		public List<ClassAssignmentInterface.ClassAssignment> getChanges() { return iChanges; }
		public void addChange(ClassAssignmentInterface.ClassAssignment ca) {
			if (iChanges == null) iChanges = new ArrayList<ClassAssignmentInterface.ClassAssignment>();
			iChanges.add(ca);
		}
		
		public boolean isGradeModeChange() {
			if (iChanges == null) return false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (ca.getGradeMode() != null) return true;
			return false;
		}
		
		public boolean isCreditChange() {
			if (iChanges == null) return false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (ca.getCreditHour() != null) return true;
			return false;
		}
		
		public boolean isAdd(Long courseId) {
			boolean hasDrop = false, hasAdd = false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (courseId.equals(ca.getCourseId())) {
					switch (ca.getSpecRegOperation()) {
					case Add: hasAdd = true; break;
					case Drop: hasDrop = true; break;
					}
				}
			return hasAdd && !hasDrop;
		}
		
		public boolean isDrop(Long courseId) {
			boolean hasDrop = false, hasAdd = false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (courseId.equals(ca.getCourseId())) {
					switch (ca.getSpecRegOperation()) {
					case Add: hasAdd = true; break;
					case Drop: hasDrop = true; break;
					}
				}
			return hasDrop && !hasAdd;
		}
		
		public boolean isChange(Long courseId) {
			boolean hasDrop = false, hasAdd = false, hasKeep = false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (courseId.equals(ca.getCourseId())) {
					switch (ca.getSpecRegOperation()) {
					case Add: hasAdd = true; break;
					case Drop: hasDrop = true; break;
					case Keep: hasKeep = true; break;
					}
				}
			return hasKeep || (hasDrop && hasAdd);
		}
		
		public boolean hasErrors(Long courseId) {
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (courseId.equals(ca.getCourseId()) && ca.hasError()) return true;
			return false;
		}
		
		public boolean isApproved(Long courseId) {
			boolean approved = false;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (!ca.hasError()) {
					if (ca.getSpecRegStatus() == SpecialRegistrationStatus.Approved) approved = true;
					else return false;
				}
			return approved;
		}
		
		public boolean isHonorsGradeModeNotFullyMatching(ClassAssignmentInterface saved) {
			if (!hasChanges()) return false;
			for (ClassAssignmentInterface.ClassAssignment ch: iChanges) {
				if (ch.getGradeMode() != null && ch.getGradeMode().isHonor()) {
					boolean found = false;
					for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
						if (ca.isSaved() && ch.getClassId().equals(ca.getClassId())) {
							found = true; break;
						}
					if (!found) return true;
				}
			}
			return false;
		}
		
		public boolean isFullyApplied(ClassAssignmentInterface saved) {
			if (!hasChanges() || isGradeModeChange() || isCreditChange() || isExtended()) return getStatus() == SpecialRegistrationStatus.Approved;
			if (saved == null) return false;
			Set<Long> courseIds = new HashSet<Long>();
			boolean enrolled = true, gmChange = false;
			changes: for (ClassAssignmentInterface.ClassAssignment ch: iChanges) {
				if (ch.getSpecRegOperation() == SpecialRegistrationOperation.Keep) {
					if (ch.getGradeMode() != null) {
						if (ch.getGradeMode().isHonor()) {
							boolean found = false;
							for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
								if (ca.isSaved() && ch.getClassId().equals(ca.getClassId())) {
									found = true; break;
								}
							if (!found) enrolled = false;
						}
						for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
							if (ca.isSaved() && ch.getCourseId().equals(ca.getCourseId()) && !ch.getGradeMode().equals(ca.getGradeMode())) {
								gmChange = true; break;
							}
					}
					if (ch.getCreditHour() != null) {
						for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
							if (ca.isSaved() && ch.getCourseId().equals(ca.getCourseId()) && ca.getCreditHour() != null && ca.getCreditHour().equals(ch.getCreditHour())) {
								return false;
							}
					}
					continue;
				}
				Long courseId = ch.getCourseId();
				if (courseIds.add(courseId)) {
					boolean hasDrop = false, hasAdd = false;
					for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
						if (courseId.equals(ca.getCourseId())) {
							switch (ca.getSpecRegOperation()) {
							case Add: hasAdd = true; break;
							case Drop: hasDrop = true; break;
							}
						}
					if (hasAdd && !hasDrop) {
						// continue, if the course is already added (ignore sections)
						for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
							if (ca.isSaved() && courseId.equals(ca.getCourseId())) continue changes;
						return false;
					} else if (hasDrop && !hasAdd) {
						// continue, if the course is already dropped (ignore sections)
						for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
							if (ca.isSaved() && courseId.equals(ca.getCourseId())) return false;
					} else {
						// check sections with an error
						for (ClassAssignmentInterface.ClassAssignment ca: iChanges) {
							if (courseId.equals(ca.getCourseId()) && ca.hasError()) {
								boolean match = false;
								for (ClassAssignmentInterface.ClassAssignment x: saved.getClassAssignments()) {
									if (x.isSaved() && ca.getClassId().equals(x.getClassId())) { match = true; break; }
								}
								// drop operation but section was found
								if (match && ca.getSpecRegOperation() == SpecialRegistrationOperation.Drop) return false;
								// add operation but section was NOT found
								if (!match && ca.getSpecRegOperation() == SpecialRegistrationOperation.Add) return false;
							}
						}
					}
				}
			}
			if (gmChange && enrolled) return false;
			return true;
		}
		
		public boolean isApplied(Long courseId, ClassAssignmentInterface saved) {
			if (courseId == null || saved == null) return false;
			boolean hasDrop = false, hasAdd = false, hasKeep = false;
			GradeMode gm = null;
			Float vc = null;
			for (ClassAssignmentInterface.ClassAssignment ca: iChanges)
				if (courseId.equals(ca.getCourseId())) {
					switch (ca.getSpecRegOperation()) {
					case Add: hasAdd = true; break;
					case Drop: hasDrop = true; break;
					case Keep:
						if (ca.getGradeMode() != null)
							gm = ca.getGradeMode();
						if (ca.getCreditHour() != null)
							vc = ca.getCreditHour();
						if (ca.getGradeMode() == null && ca.getCreditHour() == null)
							hasKeep = true;
						break;
					}
				}
			if (gm != null) {
				for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
					if (ca.isSaved() && courseId.equals(ca.getCourseId())) {
						if (ca.getGradeMode() != null && !ca.getGradeMode().equals(gm)) return false;
						if (vc != null && ca.getCreditHour() != null && !ca.getCreditHour().equals(vc)) return false;
					}
				return true;
			} else if (vc != null) {
				for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
					if (ca.isSaved() && courseId.equals(ca.getCourseId())) {
						if (ca.getCreditHour() != null && !ca.getCreditHour().equals(vc)) return false;
					}
				return true;
			} else if (hasKeep) {
				return false;
			} else if (hasAdd && !hasDrop) {
				// course is already added (ignore sections)
				for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
					if (ca.isSaved() && courseId.equals(ca.getCourseId())) return true;
				return false;
			} else if (hasDrop && !hasAdd) {
				// course is already dropped (ignore sections)
				for (ClassAssignmentInterface.ClassAssignment ca: saved.getClassAssignments())
					if (ca.isSaved() && courseId.equals(ca.getCourseId())) return false;
				return true;
			} else {
				// course is changed, check sections with errors
				for (ClassAssignmentInterface.ClassAssignment ca: iChanges) 
					if (courseId.equals(ca.getCourseId()) && ca.hasError()) {
						boolean match = false;
						for (ClassAssignmentInterface.ClassAssignment x: saved.getClassAssignments()) {
							if (x.isSaved() && ca.getClassId().equals(x.getClassId())) {
								match = true; break;
							}
						}
						if (match && ca.getSpecRegOperation() == SpecialRegistrationOperation.Drop) return false;
						if (!match && ca.getSpecRegOperation() == SpecialRegistrationOperation.Add) return false;
					}
				return true;
			}
		}
		
		public boolean canCancel() { return iCanCancel; }
		public void setCanCancel(boolean canCancel) { iCanCancel = canCancel; }
		
		public boolean hasTimeConflict() { return iHasTimeConflict; }
		public void setHasTimeConflict(boolean hasTimeConflict) { iHasTimeConflict = hasTimeConflict; }
		
		public boolean hasSpaceConflict() { return iHasSpaceConflict; }
		public void setHasSpaceConflict(boolean hasSpaceConflict) { iHasSpaceConflict = hasSpaceConflict; }
		
		public boolean isExtended() { return iExtended; }
		public void setExtended(boolean extended) { iExtended = extended; }
		
		public void addError(ErrorMessage error) {
			if (iErrors == null) iErrors = new ArrayList<ErrorMessage>();
			iErrors.add(error);
		}
		public boolean hasErrors() {
			return iErrors != null && !iErrors.isEmpty();
		}
		public ArrayList<ErrorMessage> getErrors() { return iErrors; }
		
		public void setMaxCredit(Float maxCredit) { iMaxCredit = maxCredit; }
		public Float getMaxCredit() { return iMaxCredit; }
		public boolean hasMaxCredit() { return iMaxCredit != null; }
		
		@Override
		public int compareTo(RetrieveSpecialRegistrationResponse o) {
			int cmp = getSubmitDate().compareTo(o.getSubmitDate());
			if (cmp != 0) return -cmp;
			return getRequestId().compareTo(o.getRequestId());
		}
		
		public int hashCode() {
			return getRequestId().hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof RetrieveSpecialRegistrationResponse)) return false;
			return getRequestId().equals(((RetrieveSpecialRegistrationResponse)o).getRequestId());
		}
	}
	
	public static class SubmitSpecialRegistrationRequest implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		private String iRequestId;
		private CourseRequestInterface iCourses;
		private Collection<ClassAssignmentInterface.ClassAssignment> iClassAssignments;
		private ArrayList<ErrorMessage> iErrors = null;
		private String iNote;
		private Float iCredit;
		
		public SubmitSpecialRegistrationRequest() {}
		public SubmitSpecialRegistrationRequest(Long sessionId, Long studentId, String requestId, CourseRequestInterface courses, Collection<ClassAssignmentInterface.ClassAssignment> assignments, Collection<ErrorMessage> errors, String note, Float credit) {
			iRequestId = requestId;
			iStudentId = studentId;
			iSessionId = sessionId;
			iCourses = courses;
			iClassAssignments = assignments;
			if (errors != null)
				iErrors = new ArrayList<ErrorMessage>(errors);
			iNote = note;
			iCredit = credit;
		}
		
		public Collection<ClassAssignmentInterface.ClassAssignment> getClassAssignments() { return iClassAssignments; }
		public void setClassAssignments(Collection<ClassAssignmentInterface.ClassAssignment> assignments) { iClassAssignments = assignments; }
		public CourseRequestInterface getCourses() { return iCourses; }
		public void setCourses(CourseRequestInterface courses) { iCourses = courses; }
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
		public String getRequestId() { return iRequestId; }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		public void addError(ErrorMessage error) {
			if (iErrors == null) iErrors = new ArrayList<ErrorMessage>();
			iErrors.add(error);
		}
		public boolean hasErrors() {
			return iErrors != null && !iErrors.isEmpty();
		}
		public ArrayList<ErrorMessage> getErrors() { return iErrors; }
		public String getNote() { return iNote; }
		public void setNote(String note) { iNote = note; }
		public void setCredit(Float credit) { iCredit = credit; }
		public boolean hasCredit() { return iCredit != null; }
		public Float getCredit() { return iCredit; }
	}
	
	public static class SubmitSpecialRegistrationResponse implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private String iRequestId;
		private String iMessage;
		private boolean iSuccess;
		private SpecialRegistrationStatus iStatus = null;
		private List<RetrieveSpecialRegistrationResponse> iRequests = null;
		private Set<String> iCancelledRequestIds;
		
		public SubmitSpecialRegistrationResponse() {}
		
		public String getRequestId() { return iRequestId; }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		
		public boolean hasMessage() { return iMessage != null && !iMessage.isEmpty(); }
		public String getMessage() { return iMessage; }
		public void setMessage(String message) { iMessage = message; }
		
		public boolean isSuccess() { return iSuccess; }
		public boolean isFailure() { return !iSuccess; }
		public void setSuccess(boolean success) { iSuccess = success; }
		
		public SpecialRegistrationStatus getStatus() { return iStatus; }
		public void setStatus(SpecialRegistrationStatus status) { iStatus = status; }
		
		public List<RetrieveSpecialRegistrationResponse> getRequests() { return iRequests; }
		public void addRequest(RetrieveSpecialRegistrationResponse request) {
			if (iRequests == null) iRequests = new ArrayList<RetrieveSpecialRegistrationResponse>();
			iRequests.add(request);
		}
		public boolean hasRequests() { return iRequests != null && !iRequests.isEmpty(); }
		public boolean hasRequest(String requestId) {
			if (iRequests == null) return false;
			for (RetrieveSpecialRegistrationResponse r: iRequests)
				if (requestId.equals(r.getRequestId())) return true;
			return false;
		}
		
		public void addCancelledRequest(String requestId) {
			if (iCancelledRequestIds == null) iCancelledRequestIds = new HashSet<String>();
			iCancelledRequestIds.add(requestId);
		}
		public boolean hasCancelledRequestIds() { return iCancelledRequestIds != null && !iCancelledRequestIds.isEmpty(); }
		public Set<String> getCancelledRequestIds() { return iCancelledRequestIds; }
		public boolean isCancelledRequest(String requestId) { return iCancelledRequestIds != null && iCancelledRequestIds.contains(requestId); }
	}
	
	public static class RetrieveAllSpecialRegistrationsRequest implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		
		public RetrieveAllSpecialRegistrationsRequest() {}
		public RetrieveAllSpecialRegistrationsRequest(Long sessionId, Long studentId) {
			iStudentId = studentId;
			iSessionId = sessionId;
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
	}
	
	public static class CancelSpecialRegistrationRequest implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		private String iRequestKey;
		private String iRequestId;
		
		public CancelSpecialRegistrationRequest() {}
		public CancelSpecialRegistrationRequest(Long sessionId, Long studentId, String requestKey, String requestId) {
			iRequestKey = requestKey;
			iRequestId = requestId;
			iStudentId = studentId;
			iSessionId = sessionId;
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
		public String getRequestId() { return iRequestId; }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		public String getRequestKey() { return iRequestKey; }
		public void setRequestKey(String requestKey) { iRequestKey = requestKey; }
	}
	
	public static class CancelSpecialRegistrationResponse implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private boolean iSuccess;
		private String iMessage;
		
		public CancelSpecialRegistrationResponse() {}
		
		public boolean isSuccess() { return iSuccess; }
		public boolean isFailure() { return !iSuccess; }
		public void setSuccess(boolean success) { iSuccess = success; }
		
		public boolean hasMessage() { return iMessage != null && !iMessage.isEmpty(); }
		public String getMessage() { return iMessage; }
		public void setMessage(String message) { iMessage = message; }
	}
	
	public static class RetrieveAvailableGradeModesRequest implements GwtRpcRequest<RetrieveAvailableGradeModesResponse>, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		
		public RetrieveAvailableGradeModesRequest() {}
		public RetrieveAvailableGradeModesRequest(Long sessionId, Long studentId) {
			iStudentId = studentId;
			iSessionId = sessionId;
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
	}
	
	public static class RetrieveAvailableGradeModesResponse implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		Map<String, SpecialRegistrationGradeModeChanges> iModes = new HashMap<String, SpecialRegistrationGradeModeChanges>();
		Map<String, SpecialRegistrationVariableCreditChange> iVarCreds = new HashMap<String, SpecialRegistrationVariableCreditChange>();
		private Float iMaxCredit, iCurrentCredit;
		
		public RetrieveAvailableGradeModesResponse() {}
		
		public boolean hasGradeModes() { return !iModes.isEmpty(); }
		
		public void add(String sectionId, SpecialRegistrationGradeModeChanges modes) {
			iModes.put(sectionId, modes);
		}
		
		public boolean hasVariableCredits() { return !iVarCreds.isEmpty(); }
		
		public void add(String sectionId, SpecialRegistrationVariableCreditChange var) {
			iVarCreds.put(sectionId,var);
		}
		
		public SpecialRegistrationGradeModeChanges get(ClassAssignment a) {
			if (a.getExternalId() == null) return null;
			if (a.getParentSection() != null && a.getParentSection().equals(a.getSection())) return null;
			return iModes.get(a.getExternalId());
		}
		
		public SpecialRegistrationVariableCreditChange getVariableCredits(ClassAssignment a) {
			if (a.getExternalId() == null) return null;
			if (a.getParentSection() != null && a.getParentSection().equals(a.getSection())) return null;
			return iVarCreds.get(a.getExternalId());
		}
		
		public Float getMaxCredit() { return iMaxCredit; }
		public void setMaxCredit(Float credit) { iMaxCredit = credit; }
		
		public Float getCurrentCredit() { return iCurrentCredit; }
		public void setCurrentCredit(Float credit) { iCurrentCredit = credit; }
	}
	
	public static class SpecialRegistrationGradeMode extends GradeMode {
		private static final long serialVersionUID = 1L;
		private List<String> iApprovals = null;
		private String iDisclaimer = null;
		private String iOriginalGradeMode = null;
		
		public SpecialRegistrationGradeMode() {
			super();
		}
		public SpecialRegistrationGradeMode(String code, String label, boolean honors) {
			super(code, label, honors);
		}
		
		public boolean hasApprovals() { return iApprovals != null && !iApprovals.isEmpty(); }
		public List<String> getApprovals() { return iApprovals; }
		public void addApproval(String approval) {
			if (iApprovals == null) iApprovals = new ArrayList<String>();
			iApprovals.add(approval);
		}
		
		public boolean hasDisclaimer() { return iDisclaimer != null && !iDisclaimer.isEmpty(); }
		public String getDisclaimer() { return iDisclaimer; }
		public void setDisclaimer(String disclaimer) { iDisclaimer = disclaimer; }
		
		public String getOriginalGradeMode() { return iOriginalGradeMode; }
		public void setOriginalGradeMode(String mode) { iOriginalGradeMode = mode; }
	}
	
	public static class SpecialRegistrationVariableCredit implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private List<String> iApprovals = null;
		private Float iCredit = null;
		private Float iOriginalCredit = null;
		
		public SpecialRegistrationVariableCredit() {
			super();
		}
		public SpecialRegistrationVariableCredit(SpecialRegistrationVariableCreditChange change) {
			super();
			if (change.hasApprovals()) iApprovals = new ArrayList<String>(change.getApprovals());
		}
		
		public boolean hasApprovals() { return iApprovals != null && !iApprovals.isEmpty(); }
		public List<String> getApprovals() { return iApprovals; }
		public void addApproval(String approval) {
			if (iApprovals == null) iApprovals = new ArrayList<String>();
			iApprovals.add(approval);
		}
		
		public Float getOriginalCredit() { return iOriginalCredit; }
		public void setOriginalCredit(Float credit) { iOriginalCredit = credit; }
		
		public Float getCredit() { return iCredit; }
		public void setCredit(Float credit) { iCredit = credit; }
		
		public float getCreditChange() { return (iCredit == null ? 0f : iCredit.floatValue()) - (iOriginalCredit == null ? 0f : iOriginalCredit.floatValue()); }
	}
	
	public static class SpecialRegistrationGradeModeChanges implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private SpecialRegistrationGradeMode iCurrentGradeMode;
		private Set<SpecialRegistrationGradeMode> iAvailableChanges;
		
		public SpecialRegistrationGradeModeChanges() {}
		
		public SpecialRegistrationGradeMode getCurrentGradeMode() { return iCurrentGradeMode; }
		public void setCurrentGradeMode(SpecialRegistrationGradeMode mode) { iCurrentGradeMode = mode; }
		public boolean isCurrentGradeMode(String code) {
			return iCurrentGradeMode != null && iCurrentGradeMode.getCode().equals(code);
		}
		
		public void addAvailableChange(SpecialRegistrationGradeMode mode) {
			if (iAvailableChanges == null) iAvailableChanges = new TreeSet<SpecialRegistrationGradeMode>();
			iAvailableChanges.add(mode);
		}
		public boolean hasAvailableChanges() { return iAvailableChanges != null && !iAvailableChanges.isEmpty(); }
		public Set<SpecialRegistrationGradeMode> getAvailableChanges() { return iAvailableChanges ;}
		public SpecialRegistrationGradeMode getAvailableChange(String code) {
			if (iAvailableChanges == null) return null;
			for (SpecialRegistrationGradeMode m: iAvailableChanges)
				if (m.getCode().equals(code)) return m;
			return null;
		}
	}
	
	public static class SpecialRegistrationGradeModeChange implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private String iSubject, iCourse, iCredit;
		private Set<String> iCrn;
		private Set<String> iApprovals = null;
		private String iOriginalGradeMode = null;
		private String iSelectedGradeMode = null;
		private String iSelectedGradeModeDescription = null;
		
		
		public SpecialRegistrationGradeModeChange() {}
		
		public String getSubject() { return iSubject; }
		public void setSubject(String subject) { iSubject = subject; }
		
		public String getCourse() { return iCourse; }
		public void setCourse(String course) { iCourse = course; }
		
		public String getCredit() { return iCredit; }
		public void setCredit(String credit) { iCredit = credit; }
		
		public String getOriginalGradeMode() { return iOriginalGradeMode; }
		public void setOriginalGradeMode(String gm) { iOriginalGradeMode = gm; }
		
		public String getSelectedGradeMode() { return iSelectedGradeMode; }
		public void setSelectedGradeMode(String gm) { iSelectedGradeMode = gm; }
		
		public String getSelectedGradeModeDescription() { return iSelectedGradeModeDescription; }
		public void setSelectedGradeModeDescription(String desc) { iSelectedGradeModeDescription = desc; }
		
		public boolean hasCRNs() { return iCrn != null && iCrn.isEmpty(); }
		public void addCrn(String crn) {
			if (iCrn == null) iCrn = new TreeSet<String>();
			iCrn.add(crn);
		}
		public Set<String> getCRNs() { return iCrn; }
		public boolean hasCRN(String extId) { return iCrn != null && iCrn.contains(extId); }
		
		public boolean hasApprovals() { return iApprovals != null && !iApprovals.isEmpty(); }
		public void addApproval(String app) {
			if (iApprovals == null) iApprovals = new TreeSet<String>();
			iApprovals.add(app);
		}
		public Set<String> getApprovals() { return iApprovals; }
	}
	
	public static class SpecialRegistrationCreditChange implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private String iSubject, iCourse, iCrn;
		private Float iCredit, iOriginalCredit;
		private Set<String> iApprovals = null;
		
		public SpecialRegistrationCreditChange() {}
		
		public String getSubject() { return iSubject; }
		public void setSubject(String subject) { iSubject = subject; }
		
		public String getCourse() { return iCourse; }
		public void setCourse(String course) { iCourse = course; }

		public String getCrn() { return iCrn; }
		public void setCrn(String crn) { iCrn = crn; }
		
		public Float getOriginalCredit() { return iOriginalCredit; }
		public void setOriginalCredit(Float credit) { iOriginalCredit = credit; }
		
		public Float getCredit() { return iCredit; }
		public void setCredit(Float credit) { iCredit = credit; }

		public boolean hasApprovals() { return iApprovals != null && !iApprovals.isEmpty(); }
		public void addApproval(String app) {
			if (iApprovals == null) iApprovals = new TreeSet<String>();
			iApprovals.add(app);
		}
		public Set<String> getApprovals() { return iApprovals; }

	}
	
	public static class SpecialRegistrationVariableCreditChange implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Set<String> iApprovals = null;
		private Set<Float> iAvailableCredits = null;
		
		public SpecialRegistrationVariableCreditChange() {}

		public boolean hasApprovals() { return iApprovals != null && !iApprovals.isEmpty(); }
		public void addApproval(String app) {
			if (iApprovals == null) iApprovals = new TreeSet<String>();
			iApprovals.add(app);
		}
		public Set<String> getApprovals() { return iApprovals; }
		
		public boolean hasAvailableCredits() { return iAvailableCredits != null && !iAvailableCredits.isEmpty(); }
		public void addAvailableCredit(Float credit) {
			if (iAvailableCredits == null) iAvailableCredits = new TreeSet<Float>();
			iAvailableCredits.add(credit);
		}
		public Set<Float> getAvailableCredits() { return iAvailableCredits; }
	}
	
	public static class ChangeGradeModesRequest implements GwtRpcRequest<ChangeGradeModesResponse>, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		List<SpecialRegistrationGradeModeChange> iChanges = new ArrayList<SpecialRegistrationGradeModeChange>();
		List<SpecialRegistrationCreditChange> iCreditChanges = new ArrayList<SpecialRegistrationCreditChange>();
		private String iNote;
		private Float iMaxCredit, iCurrentCredit;
		
		public ChangeGradeModesRequest() {}
		public ChangeGradeModesRequest(Long sessionId, Long studentId) {
			iStudentId = studentId;
			iSessionId = sessionId;
		}
		
		public boolean hasGradeModeChanges() { return !iChanges.isEmpty(); }
		
		public void addChange(SpecialRegistrationGradeModeChange change) {
			iChanges.add(change);
		}
		
		public SpecialRegistrationGradeModeChange getChange(String sectionId) {
			for (SpecialRegistrationGradeModeChange ch: iChanges)
				if (ch.hasCRN(sectionId)) return ch;
			return null;
		}
		
		public List<SpecialRegistrationGradeModeChange> getChanges() {
			return iChanges;
		}
		
		public boolean hasGradeModeChanges(boolean approval) {
			for (SpecialRegistrationGradeModeChange change: iChanges) {
				if (approval && change.hasApprovals()) return true;
				if (!approval && !change.hasApprovals()) return true;
			}
			return false;
		}
		
		public boolean hasCreditChanges() { return !iCreditChanges.isEmpty(); }
		
		public void addChange(SpecialRegistrationCreditChange change) {
			iCreditChanges.add(change);
		}
		
		public SpecialRegistrationCreditChange getCreditChange(String sectionId) {
			for (SpecialRegistrationCreditChange ch: iCreditChanges)
				if (ch.getCrn().equals(sectionId)) return ch;
			return null;
		}
		
		public List<SpecialRegistrationCreditChange> getCreditChanges() {
			return iCreditChanges;
		}
		
		public boolean hasCreditChanges(boolean approval) {
			for (SpecialRegistrationCreditChange change: iCreditChanges) {
				if (approval && change.hasApprovals()) return true;
				if (!approval && !change.hasApprovals()) return true;
			}
			return false;
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
		public void setNote(String note) { iNote = note; }
		public String getNote() { return iNote; }
		public boolean hasNote() { return iNote != null && !iNote.isEmpty(); }
		
		public Float getMaxCredit() { return iMaxCredit; }
		public void setMaxCredit(Float credit) { iMaxCredit = credit; }
		
		public Float getCurrentCredit() { return iCurrentCredit; }
		public void setCurrentCredit(Float credit) { iCurrentCredit = credit; }
	}
	
	public static class ChangeGradeModesResponse implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		private GradeModes iGradeModes = null;
		private List<RetrieveSpecialRegistrationResponse> iRequests = null;
		private Set<String> iCancelRequestIds = null;
		
		public ChangeGradeModesResponse() {}
		
		public boolean hasGradeModes() {
			return iGradeModes != null && iGradeModes.hasGradeModes();
		}
		public void addGradeMode(String sectionId, String code, String label, boolean honors) {
			if (iGradeModes == null) iGradeModes = new GradeModes();
			iGradeModes.addGradeMode(sectionId, new GradeMode(code, label, honors));
		}
		public GradeMode getGradeMode(ClassAssignment section) {
			if (iGradeModes == null) return null;
			return iGradeModes.getGradeMode(section);
		}
		public GradeModes getGradeModes() { return iGradeModes; }
		
		public boolean hasCreditHours() {
			return iGradeModes != null && iGradeModes.hasCreditHours();
		}
		public void addCreditHour(String sectionId, Float creditHour) {
			if (iGradeModes == null) iGradeModes = new GradeModes();
			iGradeModes.addCreditHour(sectionId, creditHour);
		}
		public Float getCreditHour(ClassAssignment section) {
			if (iGradeModes == null) return null;
			return iGradeModes.getCreditHour(section);
		}
		
		public boolean hasRequests() { return iRequests != null && !iRequests.isEmpty(); }
		public void addRequest(RetrieveSpecialRegistrationResponse request) {
			if (iRequests == null) iRequests = new ArrayList<RetrieveSpecialRegistrationResponse>();
			iRequests.add(request);
		}
		public List<RetrieveSpecialRegistrationResponse> getRequests() { return iRequests; }
		public boolean hasRequest(String requestId) {
			if (iRequests == null) return false;
			for (RetrieveSpecialRegistrationResponse r: iRequests)
				if (r.getRequestId().equals(requestId)) return true;
			return false;
		}
		
		public void addCancelRequestId(String id) {
			if (iCancelRequestIds == null) iCancelRequestIds = new HashSet<String>();
			iCancelRequestIds.add(id);
		}
		public boolean hasCancelRequestIds() { return iCancelRequestIds != null && !iCancelRequestIds.isEmpty(); }
		public Set<String> getCancelRequestIds() { return iCancelRequestIds; }
		public boolean isToBeCancelled(String requestId) { return iCancelRequestIds != null && iCancelRequestIds.contains(requestId); }
	}
	
	public static interface ChangeRequestorNoteInterface {
		public boolean changeRequestorNote(RequestedCourse request);
		public boolean changeRequestorCreditNote(CourseRequestInterface request);
		public boolean changeRequestorNote(RetrieveSpecialRegistrationResponse registration);
	}
	
	public static class UpdateSpecialRegistrationRequest implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iSessionId;
		private Long iStudentId;
		private String iRequestId;
		private String iNote;
		private boolean iPreReg = false;
		
		public UpdateSpecialRegistrationRequest() {}
		public UpdateSpecialRegistrationRequest(Long sessionId, Long studentId, String requestId, String note, boolean preReg) {
			iRequestId = requestId;
			iStudentId = studentId;
			iSessionId = sessionId;
			iNote = note;
			iPreReg = preReg;
		}
		
		public Long getSessionId() { return iSessionId; }
		public void setSessionId(Long sessionId) { iSessionId = sessionId; }
		public Long getStudentId() { return iStudentId; }
		public void setStudentId(Long studentId) { iStudentId = studentId; }
		public String getRequestId() { return iRequestId; }
		public void setRequestId(String requestId) { iRequestId = requestId; }
		public String getNote() { return iNote; }
		public void setNote(String note) { iNote = note; }
		public boolean isPreReg() { return iPreReg; }
		public void setPreReg(boolean preReg) { iPreReg = preReg; }
	}
	
	public static class UpdateSpecialRegistrationResponse implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private boolean iSuccess;
		private String iMessage;
		
		public UpdateSpecialRegistrationResponse() {}
		
		public boolean isSuccess() { return iSuccess; }
		public boolean isFailure() { return !iSuccess; }
		public void setSuccess(boolean success) { iSuccess = success; }
		
		public boolean hasMessage() { return iMessage != null && !iMessage.isEmpty(); }
		public String getMessage() { return iMessage; }
		public void setMessage(String message) { iMessage = message; }
	}
}
