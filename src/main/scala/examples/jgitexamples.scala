package examples

import java.io.File
import java.util.Date

import com.jcraft.jsch.Session
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{ObjectId, Ref, Repository, RepositoryCache}
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport._
import org.eclipse.jgit.util.FS

import scala.collection.immutable
import scala.util.{Failure, Try}


object git {

  import scala.collection.JavaConverters._

  case class GitSupport(internalGit: Git, repository: Repository, isNewClone: Boolean)

  case class TagDetail(ref: Ref, creationTime: Date, version: String, createdBy: String, tagMessage: String)

  type Revision = String

  def cloneRepository(remoteURL: String, repoDirectory: String)(isAlreadyCloned: String ⇒ Boolean): GitSupport = {
    val alreadyCloned = isAlreadyCloned(repoDirectory)
    val git =
      if (!alreadyCloned) {
        Git.cloneRepository().setURI(remoteURL).setDirectory(new File(repoDirectory)).call()
      } else {
        val localGit = Git.open(new File(repoDirectory), FS.DETECTED)
        //localGit.fetchTags(remoteURL)
        localGit
      }
    GitSupport(git, git.getRepository, isNewClone = !alreadyCloned)
  }

  def configureJsch(): Unit = {
    SshSessionFactory.setInstance(new JschConfigSessionFactory {
      def configure(hc: Host, session: Session): Unit =
        session.setConfig("StrictHostKeyChecking", "no")
    })
  }

  def latestTag(givenTags: Seq[TagDetail]): Option[TagDetail] =
    givenTags.sortBy(-_.creationTime.getTime).headOption

  implicit class GitCloneResultSyntax(jgit: GitSupport) {
    private val git = jgit.internalGit
    private val remoteURL = jgit.repository.getConfig.getString( "remote", "origin", "url" )

    def describe(): String  =
      git.describe().setLong(true).call()

    def localTags(): List[Ref] =
      git.tagList().call().asScala.toList

    def remoteTags(): List[Ref] =
      git.lsRemote().setTags(true).call().asScala.toList

    def fetchTags(): FetchResult =
      git.fetch().setTagOpt(TagOpt.FETCH_TAGS)
        .setRemote(remoteURL)
        .setRefSpecs(new RefSpec("+refs/tags/*:refs/tags/*"))
        .call()


    def newTags(): List[Ref] = {
      val lts = localTags()
      val rts = remoteTags()

      if (jgit.isNewClone) {
        lts
      } else {
        rts
          .map(_.getObjectId)
          .diff(lts.map(_.getObjectId))
          .flatMap(refId ⇒ rts.filter(_.getObjectId == refId))
      }
    }

    def eligibleTags(refs: List[Ref]): Seq[TagDetail] = {
      fetchTags()
      val walk = new RevWalk(jgit.repository, 1)
      refs.flatMap { ref =>
        Try {
          val revTag = walk.parseTag(ref.getObjectId)
          val triggerIndent = revTag.getTaggerIdent
          val revision = ref.actualRefObjectId(jgit.repository).getName
          TagDetail(ref, triggerIndent.getWhen, revision, triggerIndent.getName, revTag.getShortMessage)
        }.toOption
      }
    }


    def recentTag: Option[Ref] = {
      val candidateTags = newTags()

      if (candidateTags.nonEmpty) {
        fetchTags()

        val walk = new RevWalk(jgit.repository, 1)
        val orderedTags =
          candidateTags
            .sortBy { ref ⇒
            val ts = Try(walk.parseTag(ref.getObjectId).getTaggerIdent.getWhen).recover {
              case _ ⇒ new Date(0)
            }.get
            -ts.getTime
          }

        candidateTags
          .foreach{ ref ⇒
            val ts = Try(walk.parseTag(ref.getObjectId).getTaggerIdent.getWhen).recover {
              case _ ⇒ new Date(0)
            }.get
            println(ref.getName + "----"+ts.getTime)
          }

        println("Found following recent Tag: "+ orderedTags.head)
        Some(orderedTags.head)
      }
      else
        None
    }

    def checkout(newTag: Option[TagDetail]): Option[Revision] = {
      newTag.map { tagDetail: TagDetail ⇒
        println("Checking out "+ tagDetail.ref)
        fetchTags()
        git.checkout().setName(tagDetail.ref.getObjectId.getName).call()
        tagDetail.version
      }
    }
  }

  implicit  class RefSyntax(ref: Ref) {
    def actualRefObjectId(repository: Repository): ObjectId = {
      val repoPeeled = repository.peel(ref)
      if (repoPeeled.getPeeledObjectId != null)
        repoPeeled.getPeeledObjectId
      else
        ref.getObjectId
    }
  }

  def checkoutLatestTag(repoUrl: String, localDir: String, isRepoExists: String ⇒ Boolean): Option[Revision] = {
    configureJsch()

    val gitRepository  = cloneRepository(repoUrl, repoUrl)(isRepoExists)
    val eligibleTags   = gitRepository.eligibleTags(gitRepository.newTags())
    val maybeRecentTag = latestTag(eligibleTags)

    println("Recent Tags "+ maybeRecentTag.getOrElse("No Recent Tag Found!"))

    gitRepository.checkout(maybeRecentTag)
  }
}

object JGitExamplesApp extends App {

  import git._

  val isRepoExists: String ⇒ Boolean = path ⇒
    RepositoryCache.FileKey.isGitRepository(new File(path + ".git"), FS.DETECTED)

  val repoURL = "git@github.com:adilakhter/scalaznoob.git"
  val localRepoDirectory = "/tmp/repoTest101/"
  val relativePathToDSLDir = "src/main/mussasabi"

  val revisionMaybe = checkoutLatestTag(repoURL, localRepoDirectory, isRepoExists)

  if(revisionMaybe.isEmpty) {
    println("No Revision to update")
  } else {
    //publishDSLs(localRepoDirectory+"/"+relativePathToDSLDir, revisionMaybe)
  }
}



