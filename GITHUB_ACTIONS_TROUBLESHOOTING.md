# GitHub Actions Troubleshooting Guide

## 🚨 Common Error: "Resource not accessible by integration"

This error typically occurs due to permissions issues. Here's how to fix it:

### 1. 🔧 Repository Settings

#### Enable GitHub Actions
1. Go to your repository on GitHub
2. Click **Settings** tab
3. Go to **Actions** > **General**
4. Under "Actions permissions", select:
   - ✅ **Allow all actions and reusable workflows**
5. Click **Save**

#### Enable Package Permissions
1. In repository **Settings**
2. Go to **Actions** > **General**
3. Scroll down to "Workflow permissions"
4. Select:
   - ✅ **Read and write permissions**
   - ✅ **Allow GitHub Actions to create and approve pull requests**
5. Click **Save**

### 2. 🔑 Required Secrets Setup

Go to **Settings** > **Secrets and variables** > **Actions** and add:

| Secret Name | Value | Description |
|-------------|-------|-------------|
| `SSH_PRIVATE_KEY` | Your private key content | Full content of `~/.ssh/id_ed25519` |
| `SERVER_HOST` | `100.82.121.103` | Your server IP |
| `SERVER_USER` | `shipan` | SSH username |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `19980223` | Database password |

#### How to get SSH Private Key:
```bash
# On your local machine
cat ~/.ssh/id_ed25519

# Copy the ENTIRE output including:
# -----BEGIN OPENSSH PRIVATE KEY-----
# ... key content ...
# -----END OPENSSH PRIVATE KEY-----
```

### 3. 🏗️ Repository Setup

#### Enable GitHub Container Registry
1. Go to your repository
2. Click **Settings** tab
3. Scroll down to **Features**
4. Make sure **Packages** is enabled

#### Check Repository Visibility
- If your repository is **private**, make sure you have GitHub Pro/Team
- For **public repositories**, GHCR should work by default

### 4. 🔍 Environment Setup (Optional but Recommended)

1. Go to **Settings** > **Environments**
2. Click **New environment**
3. Name it `production`
4. Add protection rules:
   - ✅ **Required reviewers** (optional)
   - ✅ **Wait timer** (optional)
5. Add environment secrets (same as above)

### 5. 🐛 Debug Steps

#### Test GitHub Token Permissions
Add this step to your workflow to debug:

```yaml
- name: Test GitHub Token
  run: |
    echo "Testing GitHub token permissions..."
    curl -H "Authorization: token ${{ secrets.GITHUB_TOKEN }}" \
         -H "Accept: application/vnd.github.v3+json" \
         https://api.github.com/user
```

#### Test Container Registry Access
```yaml
- name: Test Container Registry
  run: |
    echo "Testing container registry access..."
    echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
    docker pull hello-world
    docker tag hello-world ghcr.io/${{ github.repository }}/test:latest
    docker push ghcr.io/${{ github.repository }}/test:latest || echo "Push failed - check permissions"
```

### 6. 🔄 Alternative Solutions

#### Option 1: Use Personal Access Token (PAT)
1. Go to GitHub **Settings** (your profile, not repository)
2. **Developer settings** > **Personal access tokens** > **Tokens (classic)**
3. Generate new token with scopes:
   - ✅ `write:packages`
   - ✅ `read:packages`
   - ✅ `repo`
4. Add as repository secret: `PAT_TOKEN`
5. Update workflow to use `${{ secrets.PAT_TOKEN }}` instead of `${{ secrets.GITHUB_TOKEN }}`

#### Option 2: Use Docker Hub Instead
```yaml
env:
  REGISTRY: docker.io
  IMAGE_NAME: your-dockerhub-username/mosh-store

# Add Docker Hub secrets:
# DOCKERHUB_USERNAME
# DOCKERHUB_TOKEN
```

### 7. 📋 Verification Checklist

Before running the workflow, verify:

- [ ] Repository has Actions enabled
- [ ] Workflow permissions set to "Read and write"
- [ ] All required secrets are added
- [ ] SSH key is correct (test with `ssh -T git@github.com`)
- [ ] Server is accessible from your machine
- [ ] Docker is installed on the server

### 8. 🚀 Test Deployment Locally First

Before using GitHub Actions, test locally:

```bash
# Test Docker build
docker build -t mosh-store-test .

# Test deployment script
./test-deployment.sh

# Test manual deployment
./deploy.sh --setup-only
```

### 9. 🔧 Workflow Fixes Applied

The workflow has been updated with:
- ✅ Proper permissions at workflow level
- ✅ Fixed GITHUB_TOKEN usage
- ✅ Added environment protection
- ✅ Better error handling

### 10. 📞 Still Having Issues?

If you're still getting errors:

1. **Check the Actions tab** for detailed error logs
2. **Verify secrets** are correctly set (no extra spaces)
3. **Test SSH connection** manually: `ssh shipan@100.82.121.103`
4. **Check server logs**: `sudo journalctl -u docker`
5. **Try manual deployment** first: `./deploy.sh`

### 11. 🎯 Quick Fix Commands

```bash
# Re-add SSH key to agent
ssh-add ~/.ssh/id_ed25519

# Test server connection
ssh -T shipan@100.82.121.103 "echo 'Connection successful'"

# Verify Docker on server
ssh shipan@100.82.121.103 "docker --version && docker compose version"

# Check GitHub CLI authentication
gh auth status
```

## 🎉 Success Indicators

When everything is working correctly, you should see:
- ✅ Green checkmarks in Actions tab
- ✅ Docker image in Packages tab
- ✅ Application accessible at https://shipan.eu/api/v1/
- ✅ Containers running on server: `docker ps`

## 🔄 Next Steps After Fix

1. Push a small change to main branch
2. Watch the Actions tab for the workflow
3. Verify deployment on your server
4. Test the application endpoints
5. Set up monitoring and alerts 