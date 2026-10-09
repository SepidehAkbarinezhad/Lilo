package com.sepideh.lilo.note
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.flowOf
import com.sepideh.lilo.note.domain.model.*
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.core.domain.images.*
import com.sepideh.lilo.core.domain.model.AppLanguage
class NoteRulesTest {
 @Test fun searchIncludesContentAndNormalizesPersianLetters() {
  val notes=listOf(Note(id=1,title="One",content="كتاب"),Note(id=2,title="Other"))
  assertEquals(listOf(1L),notes.matching(NoteQuery(text="کتاب")).map { it.id })
 }
 @Test fun sortingAndUngroupedFilterAreIndependent() {
  val notes=listOf(Note(id=1,title="Z",updatedAt=20),Note(id=2,title="A",groupId=7,updatedAt=30),Note(id=3,title="B",updatedAt=20))
  assertEquals(listOf(2L,3L,1L),notes.matching(NoteQuery()).map { it.id })
  assertEquals(listOf(3L,1L),notes.matching(NoteQuery(groupId=0,sort=NoteSort.TITLE)).map { it.id })
  assertEquals(listOf(2L),notes.matching(NoteQuery(groupId=7)).map { it.id })
 }
 @Test fun userGroupEditsOnlySelectedLanguageAndDefaultsCannotBeRenamed() {
  val group=NoteGroup(titleEn="Work",titleFa="کار")
  assertEquals("Work",group.renamed("خرید",AppLanguage.FA).titleEn)
  assertEquals("خرید",group.renamed("خرید",AppLanguage.FA).titleFa)
  assertFailsWith<IllegalArgumentException> { group.copy(isDefault=true).renamed("New",AppLanguage.EN) }
 }
 @Test fun savePreservesCreationTimeAndIdOnRoomUpdate()=runBlocking {
  val repo=FakeRepo(Note(id=4,title="Old",createdAt=10,imageNames=listOf("old.jpg")))
  val images=FakeImages()
  val result=NoteMutations(repo,images,{50}).save(repo.note!!.copy(title=" New "),listOf(ImageSource("source")))
  assertEquals(4L,result.id);assertEquals(10L,result.createdAt);assertEquals(50L,result.updatedAt)
  assertEquals("New",result.title);assertEquals(listOf("old.jpg"),images.removed)
 }
 @Test fun failedPersistenceRollsBackOnlyNewFiles()=runBlocking {
  val repo=FakeRepo(Note(id=4,title="Old",imageNames=listOf("old.jpg")),fail=true)
  val images=FakeImages()
  assertFailsWith<IllegalStateException> { NoteMutations(repo,images,{50}).save(repo.note!!,listOf(ImageSource("source"))) }
  assertEquals(listOf("new.jpg"),images.removed)
  assertEquals(listOf("old.jpg"),repo.note!!.imageNames)
 }
 @Test fun blankAndMissingNotesDoNotImportImages()=runBlocking {
  val images=FakeImages();val mutations=NoteMutations(FakeRepo(),images,{50})
  assertFailsWith<IllegalArgumentException> { mutations.save(Note(),listOf(ImageSource("source"))) }
  assertFailsWith<IllegalArgumentException> { mutations.save(Note(id=99,title="Gone"),listOf(ImageSource("source"))) }
  assertEquals(0,images.imports)
 }
 @Test fun deleteRemovesPersistedNoteAndItsImages()=runBlocking {
  val repo=FakeRepo(Note(id=4,title="Old",imageNames=listOf("old.jpg")))
  val images=FakeImages();NoteMutations(repo,images).delete(4)
  assertNull(repo.note);assertEquals(listOf("old.jpg"),images.removed)
 }
 private class FakeRepo(var note:Note?=null,val fail:Boolean=false):NoteRepository {
  override fun getAllNotes()=flowOf(listOfNotNull(note))
  override suspend fun getNoteById(id:Long)=note?.takeIf { it.id==id }
  override suspend fun upsertNote(note:Note):Long { check(!fail);this.note=note;return if(note.id==0L) 8 else -1 }
  override suspend fun deleteNote(id:Long) { note=null }
 }
 private class FakeImages:ImageStore {
  var imports=0;val removed=mutableListOf<String>()
  override suspend fun importImages(sources:List<ImageSource>):List<String> { imports++;return listOf("new.jpg") }
  override suspend fun removeImages(names:List<String>) { removed.addAll(names) }
 }
}
