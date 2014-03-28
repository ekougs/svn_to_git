SVN to Git is a sample tool which helps to migrate an existing SVN repository to a Git one. 
It is build upon the "official" git-svn tool.
Here are the parameters which must or may be used to run the tool :

MANDATORY
--repo <URL>              The svn repository URL.

OPTIONAL
--file <path>             Location where you want the author file to be generated. By default it is 
                          created in the directory from where the tool is run.
--git-repo <path>         Location where you want the Git repo directory to be generated. By default it 
                          is created in the directory from where the tool is run.
--author-file-provided    State that the author file is provided and does not need to be generated.
