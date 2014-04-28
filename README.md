SVN to Git is a sample tool which helps to migrate an existing SVN repository to a Git one.
It is build upon the "official" git-svn tool.
Here are the parameters which must or may be used to run the tool :

MANDATORY

--repo <URL> : The svn repository URL.

OPTIONAL

--file <path> : Location where you want the author file to be generated or the location of the author file when it is provided. By default it is created in the directory from where the tool is launched.
                          
--git-repo <path> : Location where you want the Git repo directory to be generated. By default it is created in the directory from where the tool is launched.
                          
--author-file-provided : State that the author file is provided and does not need to be generated.

--author-mail : Provide an unique author mail for all users. If not provided, you should call Main.launchGitRepoCreation with an email supplier (com.bisam.svntogit.clone.MailSupplier). A possible implementation of the email supplier is an LDAP connected one getting author name, retrieving LDAP information associated to this author and returning the proper e-mail.

--repair-branches : To use if first commit pointer of some of the branches points to nothing. This option automatically links branches to previous commit in time on master.

Run "mvn clean package" to build the jar with dependencies which you can run.

It MUST be run with Java 7+ JVM.
