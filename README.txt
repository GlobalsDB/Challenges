GlobalsDB Challenge Code Submission:
 
1.    You need a GitHub account
	a. Please post a request including your GitHub ID to be added to the Challenge repo as a collaborator.
2.    The URL for the Challenge is: git@github.com:GlobalsDB/Challenges.git
	a.    Note that the above uses ssh.  SSH is the prescribed way to interact 		
		in a read/write way with the Challenges URL.  Your github account 			
		should be setup to use SSH.
3.    Setup the work directory (Command line instructions):
	a.	Clone the Challenge repo --> 
		git clone git@github.com:GlobalsDB/Challenges.git
	b.    Change to working directory --> cd Challenges/<Challenge Number (1, 2, 3 ? etc.)>
	c.    Create a new folder with named with your github id --> mkdir <YOUR_GITHUB_ID>
	d.    Add files to this working directory
4.    Submitted completed Challenge
	a.    Change to your working directory --> cd Challenges/<Challenge Number (1, 2, 3 ? etc.)>/<YOUR_GITHUB_ID>
	b.    Snapshot the files for the challenge--> git add .
	c.    Record the snapshot --> git commit -m "My Globals Challenge submission"  
		(-m "comment for the submission" is optional)
	d.    Submit the snapshot -->  git push (
		This uses the URL above for the git repository)
