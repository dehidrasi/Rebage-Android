package trashissue.rebage.presentation.camera

import android.Manifest.permission
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import trashissue.rebage.R
import trashissue.rebage.presentation.camera.component.CameraCapture
import trashissue.rebage.presentation.camera.component.rememberCameraState
import trashissue.rebage.presentation.common.BitmapUtils
import trashissue.rebage.presentation.common.component.toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun CameraActivity.CameraScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val cameraState = rememberCameraState()
        val scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }
        val permission = rememberPermissionState(permission.CAMERA) { isGranted ->
            if (!isGranted) {
                toast(R.string.camera_permission, Toast.LENGTH_LONG)
                openPermissionSettings()
                finish()
            }
        }
        val galleryLauncher = rememberGalleryLauncher(
            onSuccess = { file ->
                val intent = Intent().apply {
                    BitmapUtils.reduceSize(file = file, rotate = false)
                    putExtra(CameraActivity.KEY_IMAGE_RESULT, file)
                }
                setResult(CameraActivity.RESULT_SUCCESS, intent)
                finish()
            },
            onError = { error ->
                Timber.e("Error nih ${error?.message}")
                toast(R.string.text_unknown_error)
            }
        )


        LaunchedEffect(Unit) {
            if (!permission.status.isGranted) {
                permission.launchPermissionRequest()
            }
        }

        if (permission.status.isGranted) {
            CameraCapture(
                modifier = Modifier.fillMaxSize(),
                state = cameraState,
                onClickGallery = {
                    val intent = Intent().apply {
                        action = Intent.ACTION_GET_CONTENT
                        type = "image/*"
                    }

                    val chooser = Intent.createChooser(intent, "Choose image")
                    galleryLauncher.launch(chooser)
                },
                onClickCapture = {
                    scope.launch(Dispatchers.IO) {
                        loading = true
                        val capturedImage = cameraState.takePicture()
                        val backCamera = cameraState.selector == CameraSelector.DEFAULT_BACK_CAMERA
                        val intent = Intent().apply {
                            BitmapUtils.reduceSize(capturedImage, backCamera)
                            putExtra(CameraActivity.KEY_IMAGE_RESULT, capturedImage)
                        }
                        setResult(CameraActivity.RESULT_SUCCESS, intent)
                        loading = false
                        finish()
                    }
                },
                onClickSwitchCamera = {
                    cameraState.selector = when (cameraState.selector) {
                        CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
                        else -> CameraSelector.DEFAULT_BACK_CAMERA
                    }
                }
            )
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private fun CameraActivity.openPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

@Composable
fun rememberGalleryLauncher(
    onSuccess: (File) -> Unit,
    onError: (Exception?) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            if (result.resultCode != Activity.RESULT_OK) {
                return@rememberLauncherForActivityResult
            }
            val selectedImg: Uri = result.data?.data as Uri
            val file = selectedImg.uriToFile(context)
            onSuccess(file)
        } catch (e: Exception) {
            onError(e)
        }
    }
}

private fun Uri.uriToFile(context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = context.createTempFile()

    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

private fun Context.createTempFile(): File {
    val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("${System.currentTimeMillis()}", ".jpg", storageDir)
}
